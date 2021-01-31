package ns.fajnet.android.geobreadcrumbs.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.activities.main.MainActivity
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.LogEx
import ns.fajnet.android.geobreadcrumbs.common.Utils
import ns.fajnet.android.geobreadcrumbs.database.GeoBreadcrumbsDatabase
import ns.fajnet.android.geobreadcrumbs.database.Place
import ns.fajnet.android.geobreadcrumbs.database.Point
import ns.fajnet.android.geobreadcrumbs.database.Track
import ns.fajnet.android.geobreadcrumbs.dtos.PlaceDto
import ns.fajnet.android.geobreadcrumbs.dtos.PointDto
import ns.fajnet.android.geobreadcrumbs.dtos.TrackDto
import ns.fajnet.android.geobreadcrumbs.dtos.TrackExtendedDto
import java.text.SimpleDateFormat
import java.util.*

class GeoTrackService : Service() {

    // members -------------------------------------------------------------------------------------

    private var liveTrack: TrackExtendedDto = TrackExtendedDto.default()
    private var receivedLocations: Int = 0

    private val _recordingActive = MutableLiveData<Boolean>(false)
    private val _liveUpdate = MutableLiveData<TrackDto>()
    private val mBinder: IBinder = MyBinder()

    private lateinit var handleLocationUpdatesJob: Job
    private lateinit var recordingServiceScope: CoroutineScope
    private lateinit var serviceScope: CoroutineScope
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    // overrides -----------------------------------------------------------------------------------

    override fun onCreate() {
        super.onCreate()
        LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "onCreate")
        initialize()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "onStartCommand")

        // TODO: start service only if it is not started yet

        startForeground(Constants.SERVICE_ID_GEO_TRACK, generateNotification())

        if (checkPrerequisites()) {
            startRecording(intent?.getStringExtra(EXTRA_START_POINT_NAME))
        } else {
            stopSelf()
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onDestroy() {
        super.onDestroy()
        LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "onDestroy")
        serviceScope.cancel()
    }

    // properties ----------------------------------------------------------------------------------

    val liveUpdate: LiveData<TrackDto>
        get() = _liveUpdate

    val recordingActive: LiveData<Boolean>
        get() = _recordingActive

    // public methods ------------------------------------------------------------------------------

    fun stopRecordingTrack() {
        LogEx.i(Constants.TAG_GEO_TRACK_SERVICE, "stop recording")
        unsubscribeFromLocationUpdates()
        stopRecording()
    }

    fun addPlaceToTrack(placeName: String, location: Location?) {
        LogEx.i(Constants.TAG_GEO_TRACK_SERVICE, "add place to current track")
        addPlace(placeName, location)
    }

    fun renameTrack(track: Track, name: String) {
        serviceScope.launch {
            LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "renaming track with id: ${track.id} to $name")
            val trackDb = GeoBreadcrumbsDatabase.getInstance(applicationContext)
                .trackDao
                .get(track.id)

            if (trackDb == null) {
                LogEx.w(Constants.TAG_GEO_TRACK_SERVICE, "track with id ${track.id} not found")
                return@launch
            }

            val updatedTrackDb = trackDb.copy(name = name)

            GeoBreadcrumbsDatabase.getInstance(applicationContext)
                .trackDao
                .update(updatedTrackDb)

            LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "track with id: ${track.id} renamed")
        }
    }

    // private methods -----------------------------------------------------------------------------

    private fun initialize() {
        serviceScope = CoroutineScope(Dispatchers.IO)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "locationCallbackTriggered")
                super.onLocationResult(locationResult)

                if (_recordingActive.value == false) {
                    LogEx.i(Constants.TAG_GEO_TRACK_SERVICE, "recording not active, skipping..")
                    return
                }

                recordingServiceScope.launch {
                    for (location in locationResult.locations) {
                        LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "location received: $location")

                        // TODO: check if satellites are returned on a real device!!
                        val satellitesUsed = location.extras["satellites"]
                        LogEx.d(
                            Constants.TAG_GEO_TRACK_SERVICE,
                            "satellites used for fix: $satellitesUsed"
                        )

                        if (receivedLocations < NO_OF_STARTING_POINTS_TO_SKIP) {
                            LogEx.d(
                                Constants.TAG_GEO_TRACK_SERVICE,
                                "number of received locations is less than receivedLocationsBuffer ($receivedLocations < $NO_OF_STARTING_POINTS_TO_SKIP), skipping"
                            )
                            receivedLocations++
                            return@launch
                        }

                        val newPointDb = Point(
                            trackId = liveTrack.track.id,
                            longitude = location.longitude,
                            latitude = location.latitude,
                            altitude = location.altitude,
                            locationFixTime = location.time,
                            accuracy = location.accuracy,
                            speed = location.speed,
                            bearing = location.bearing
                        )

                        GeoBreadcrumbsDatabase.getInstance(applicationContext)
                            .pointDao
                            .insert(newPointDb)

                        LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "location persisted")

                        var track = liveTrack.track
                        val newPoint = PointDto.fromDb(newPointDb)
                        val points = liveTrack.points.toMutableList()
                        points.add(newPoint)

                        if (points.size > 1) {
                            val results = floatArrayOf(0F, 0F, 0F)
                            Location.distanceBetween(
                                points[points.size - 2].latitude,
                                points[points.size - 2].longitude,
                                newPoint.latitude,
                                newPoint.longitude,
                                results
                            )

                            val duration = System.currentTimeMillis() - points[0].locationFixTime
                            val distance = track.distance + results[0]
                            val currentSpeed = newPoint.speed
                            val averageSpeed = calculateAvgSpeed(points)
                            val maxSpeed = calculateMaxSpeed(points)
                            val currentBearing = results[2]
                            val overallBearing = calculateOverallBearing(points)
                            val noOfPoints = points.size

                            track = liveTrack.track.copy(
                                duration = duration,
                                distance = distance,
                                currentSpeed = currentSpeed,
                                averageSpeed = averageSpeed,
                                maxSpeed = maxSpeed,
                                currentBearing = currentBearing,
                                overallBearing = overallBearing,
                                noOfPoints = noOfPoints
                            )
                        }

                        val updatedTrack = liveTrack.copy(track = track, points = points)

                        updateLiveTrack(updatedTrack, UpdateType.Point)
                        LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "track update published")
                    }
                }
            }
        }
    }

    private fun initializeRecordingServiceScope() {
        handleLocationUpdatesJob = Job()
        // CHECK: is Dispatchers.Main the correct one for this CoroutineScope??
        recordingServiceScope = CoroutineScope(Dispatchers.IO + handleLocationUpdatesJob)
    }

    private fun generateNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        return NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID_GEO_TRACK)
            .setContentTitle(getString(R.string.geo_track_service_notification_title))
            .setContentText(getString(R.string.geo_track_service_notification_text))
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun checkPrerequisites(): Boolean {
        LogEx.d(
            Constants.TAG_GEO_TRACK_SERVICE,
            "hasPermission: ${Utils.isPermissionGranted(this)}, locationEnabled: ${
                Utils.isLocationEnabled(this)
            }"
        )

        return Utils.isPermissionGranted(this) && Utils.isLocationEnabled(this)
    }

    @SuppressLint("MissingPermission")
    private fun subscribeToLocationUpdates() {
        LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "subscribe to location updates")

        recordingServiceScope.launch {
            withContext(Dispatchers.Main) {
                fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(applicationContext)
                fusedLocationProviderClient.requestLocationUpdates(
                    generateLocationRequest(),
                    locationCallback,
                    Looper.myLooper()
                )
            }
        }
    }

    private fun unsubscribeFromLocationUpdates() {

        if (this::fusedLocationProviderClient.isInitialized) {
            LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "unsubscribe from location updates")
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun generateLocationRequest(): LocationRequest {
        val params = retrieveLocationSubscriptionParametersFromPreferences()

        return LocationRequest().setInterval(params.interval)
            .setFastestInterval(params.interval)
            .setSmallestDisplacement(params.distance)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    private fun retrieveLocationSubscriptionParametersFromPreferences(): LocationSubscriptionParameters {
        val precisionInterval = PreferenceManager.getDefaultSharedPreferences(this).getString(
            getString(R.string.settings_preference_tracking_precision_interval_key),
            resources.getStringArray(R.array.precision_interval_values)[1]
        ) ?: resources.getStringArray(R.array.precision_interval_values)[1]
        val precisionDistance = PreferenceManager.getDefaultSharedPreferences(this).getString(
            getString(R.string.settings_preference_tracking_precision_distance_key),
            resources.getStringArray(R.array.precision_distance_values)[0]
        ) ?: resources.getStringArray(R.array.precision_distance_values)[0]

        return LocationSubscriptionParameters(
            precisionInterval.toLong(),
            precisionDistance.toFloat()
        )
    }

    // TODO: think about accepting TrackExtended instead of TrackExtendedDto and do the mappings only here!
    private fun updateLiveTrack(track: TrackExtendedDto, updateType: UpdateType) {
        liveTrack = when (updateType) {
            UpdateType.Init -> track
            UpdateType.Place -> {
                val newTrack = liveTrack.track.copy(noOfPlaces = track.places.size)
                liveTrack.copy(track = newTrack, places = track.places)
            }
            UpdateType.Point -> {
                val newTrack = track.track.copy(noOfPlaces = liveTrack.places.size)
                liveTrack.copy(track = newTrack, points = track.points)
            }
            UpdateType.Reset -> {
                receivedLocations = 0
                track
            }
        }

        _liveUpdate.postValue(liveTrack.track)
    }

    private fun startRecording(startPlaceName: String?) {
        initializeRecordingServiceScope()
        recordingServiceScope.launch {
            LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "start recording")
            val sdf = SimpleDateFormat.getDateTimeInstance()
                .apply { timeZone = TimeZone.getTimeZone("UTC") }
            val utcTime: String = sdf.format(Date())

            // TODO: set track name to startPlaceName if not empty
            val newTrackDb = GeoBreadcrumbsDatabase.getInstance(applicationContext)
                .trackDao
                .insertAndRetrieve(Track(name = utcTime))
            val initialPlaces = mutableListOf<PlaceDto>()

            // TODO: startPointName not empty -> insert new place also (when/if places are implemented)
            // TODO: provide location to extract place data
            if (!startPlaceName.isNullOrBlank()) {
                val startPlaceDb = Place(trackId = newTrackDb.id, name = startPlaceName)
                GeoBreadcrumbsDatabase.getInstance(applicationContext)
                    .placeDao
                    .insert(startPlaceDb)
                initialPlaces.add(PlaceDto.fromDb(startPlaceDb))
            }

            val trackExtended = TrackExtendedDto(
                TrackDto.fromDb(newTrackDb),
                initialPlaces,
                listOf()
            )

            updateLiveTrack(trackExtended, UpdateType.Init)
            _recordingActive.postValue(true)
            subscribeToLocationUpdates()
        }
    }

    private fun stopRecording() {
        if (_recordingActive.value == false) {
            return
        }

        recordingServiceScope.launch {
            LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "stop recording")
            _recordingActive.postValue(false)
            val trackExtendedDb = GeoBreadcrumbsDatabase.getInstance(applicationContext)
                .trackDao
                .getExtended(liveTrack.track.id)

            if (trackExtendedDb == null) {
                LogEx.w(
                    Constants.TAG_GEO_TRACK_SERVICE,
                    "No track with id ${liveTrack.track.id}, nothing to update!"
                )
            } else {
                LogEx.d(
                    Constants.TAG_GEO_TRACK_SERVICE,
                    "updating track ${trackExtendedDb.track.id}"
                )

                val trackExtended = TrackExtendedDto.fromDb(trackExtendedDb)

                val trackDb = Track(
                    trackExtended.track.id,
                    trackExtended.track.name,
                    trackExtended.track.startTimeMillis,
                    System.currentTimeMillis(),
                    calculateDistance(trackExtended.points),
                    calculateAvgSpeed(trackExtended.points),
                    calculateMaxSpeed(trackExtended.points),
                    calculateOverallBearing(trackExtended.points),
                    trackExtended.places.size,
                    trackExtended.points.size,
                    1 // TODO: status enum
                )

                GeoBreadcrumbsDatabase.getInstance(applicationContext)
                    .trackDao
                    .update(trackDb)
                LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "update finished")
            }

            updateLiveTrack(TrackExtendedDto.default(), UpdateType.Reset)

            recordingServiceScope.cancel()
            stopForeground(true)
        }
    }

    private fun addPlace(placeName: String, location: Location?) {
        if (_recordingActive.value == false) {
            return
        }

        if (location == null) {
            LogEx.w(
                Constants.TAG_GEO_TRACK_SERVICE,
                "no location provided, new place won`t be added to track!"
            )

            return
        }

        recordingServiceScope.launch {
            LogEx.i(Constants.TAG_GEO_TRACK_SERVICE, "add place $placeName, $location")

            if (placeName.isNotBlank()) {
                val startPlaceDb = Place(
                    trackId = liveTrack.track.id,
                    name = placeName,
                    longitude = location.longitude,
                    latitude = location.latitude,
                    altitude = location.altitude,
                    locationFixTime = location.time
                )
                GeoBreadcrumbsDatabase.getInstance(applicationContext)
                    .placeDao
                    .insert(startPlaceDb)

                val places = liveTrack.places.toMutableList()
                places.add(PlaceDto.fromDb(startPlaceDb))
                val track = liveTrack.track.copy(noOfPlaces = places.size)
                val trackExtended = liveTrack.copy(track = track, places = places)

                updateLiveTrack(trackExtended, UpdateType.Place)
            }
        }
    }

    private fun calculateDistance(points: List<PointDto>): Float {
        var result = 0F

        for (i in 0..points.size - 2) {
            val start = points[i]
            val end = points[i + 1]
            val results = floatArrayOf(0F)
            Location.distanceBetween(
                start.latitude,
                start.longitude,
                end.latitude,
                end.longitude,
                results
            )

            result += results[0]
        }

        return result
    }

    private fun calculateAvgSpeed(points: List<PointDto>): Float {
        if (points.isEmpty()) {
            return 0F
        }

        var result = 0F
        points.forEach { x -> result += x.speed }
        result /= points.size

        return result
    }

    private fun calculateMaxSpeed(points: List<PointDto>): Float {
        var result = 0F
        points.forEach { x -> result = result.coerceAtLeast(x.speed) }

        return result
    }

    private fun calculateOverallBearing(points: List<PointDto>): Float {
        var result = 0F

        if (points.size > 2) {
            val start = points[0]
            val end = points[points.size - 1]
            val results = floatArrayOf(0F, 0F, 0F)
            Location.distanceBetween(
                start.latitude,
                start.longitude,
                end.latitude,
                end.longitude,
                results
            )

            result = results[2]
        }

        return result
    }

    // inner classes -------------------------------------------------------------------------------

    inner class MyBinder : Binder() {
        val service: GeoTrackService
            get() = this@GeoTrackService
    }

    // companion -----------------------------------------------------------------------------------

    companion object {

        // constants -------------------------------------------------------------------------------

        const val NO_OF_STARTING_POINTS_TO_SKIP = 3 // used for better initial precision

        // extras ----------------------------------------------------------------------------------

        const val EXTRA_START_POINT_NAME = "extra_start_point_name"
    }
}

// #################################################################################################

private class LocationSubscriptionParameters(val interval: Long, val distance: Float)

private enum class UpdateType {
    Init,
    Place,
    Point,
    Reset
}
