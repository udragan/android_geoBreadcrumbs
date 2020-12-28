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
import ns.fajnet.android.geobreadcrumbs.database.Point
import ns.fajnet.android.geobreadcrumbs.database.Track
import ns.fajnet.android.geobreadcrumbs.dtos.PointDto
import ns.fajnet.android.geobreadcrumbs.dtos.TrackDto
import ns.fajnet.android.geobreadcrumbs.dtos.TrackExtendedDto
import java.text.SimpleDateFormat
import java.util.*

class GeoTrackService : Service() {

    // members -------------------------------------------------------------------------------------

    private var recordingTrack: TrackExtendedDto = TrackExtendedDto(TrackDto(), mutableListOf())

    private val _recordingActive = MutableLiveData<Boolean>(false)
    private val _liveUpdate = MutableLiveData<TrackDto>()
    private val mBinder: IBinder = MyBinder()

    private lateinit var handleLocationUpdatesJob: Job
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

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun onDestroy() {
        super.onDestroy()
        LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "onDestroy")
    }

    // properties ----------------------------------------------------------------------------------

    val liveUpdate: LiveData<TrackDto>
        get() = _liveUpdate

    val recordingActive: LiveData<Boolean>
        get() = _recordingActive

    // public methods ------------------------------------------------------------------------------

    fun stopRecordingTrack() {
        LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "stop recording")
        unsubscribeFromLocationUpdates()
        stopRecording()
    }

    // private methods -----------------------------------------------------------------------------

    private fun initialize() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "locationCallbackTriggered")
                super.onLocationResult(locationResult)

                if (_recordingActive.value == false) {
                    LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "recording not active, skipping..")
                    return
                }

                serviceScope.launch {
                    for (location in locationResult.locations) {
                        LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "location received: $location")
                        val newPoint = Point(
                            trackId = recordingTrack.track.id,
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
                            .insert(newPoint)

                        LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "location persisted")

                        recordingTrack.points.add(PointDto.fromPoint(newPoint))

                        if (recordingTrack.points.size > 1) {
                            val results = floatArrayOf(0F, 0F, 0F)
                            Location.distanceBetween(
                                recordingTrack.points[recordingTrack.points.size - 2].latitude,
                                recordingTrack.points[recordingTrack.points.size - 2].longitude,
                                newPoint.latitude,
                                newPoint.longitude,
                                results
                            )
                            recordingTrack.track.duration =
                                System.currentTimeMillis() - recordingTrack.points[0].locationFixTime
                            recordingTrack.track.distance += results[0]
                            recordingTrack.track.currentSpeed = newPoint.speed
                            recordingTrack.track.averageSpeed =
                                calculateAvgSpeedFromPointDtos(recordingTrack.points)
                            recordingTrack.track.maxSpeed =
                                calculateMaxSpeedFromPointDtos(recordingTrack.points)
                            recordingTrack.track.currentBearing = results[2]
                            recordingTrack.track.overallBearing =
                                calculateOverallBearingFromPointDtos(recordingTrack.points)
                            recordingTrack.track.noOfPoints = recordingTrack.points.size
                            // TODO: update noofPlaces when added to db
                            //recordingTrack.track.noOfPlaces = recordingTrack.places.size
                        }

                        _liveUpdate.postValue(recordingTrack.track)

                        LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "track update published")
                    }
                }
            }
        }
    }

    private fun initializeServiceScope() {
        handleLocationUpdatesJob = Job()
        // CHECK: is Dispatchers.Main the correct one for this CoroutineScope??
        serviceScope = CoroutineScope(Dispatchers.IO + handleLocationUpdatesJob)
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

        serviceScope.launch {
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

    private fun startRecording(startPointName: String?) {
        initializeServiceScope()
        serviceScope.launch {
            LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "start recording")
            val sdf = SimpleDateFormat.getDateTimeInstance()
                .apply { timeZone = TimeZone.getTimeZone("UTC") }
            val utcTime: String = sdf.format(Date())

            val newTrack = Track(name = utcTime)

            val insertedTrack = GeoBreadcrumbsDatabase.getInstance(applicationContext)
                .trackDao
                .insertAndRetrieve(newTrack)
            recordingTrack.track = TrackDto.fromTrack(insertedTrack)
            _recordingActive.postValue(true)
            // TODO: startPointName not empty -> insert new place also (when/if places are implemented)
            subscribeToLocationUpdates()
        }
    }

    private fun stopRecording() {
        if (_recordingActive.value == false) {
            return
        }

        serviceScope.launch {
            LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "stop recording")
            _recordingActive.postValue(false)
            val trackWithPointsFromDb = GeoBreadcrumbsDatabase.getInstance(applicationContext)
                .trackDao
                .getWithPoints(recordingTrack.track.id)

            if (trackWithPointsFromDb == null) {
                LogEx.w(
                    Constants.TAG_GEO_TRACK_SERVICE,
                    "No track with id ${recordingTrack.track.id}, nothing to update!"
                )
            } else {
                LogEx.d(
                    Constants.TAG_GEO_TRACK_SERVICE,
                    "updating track ${recordingTrack.track.id}"
                )
                val existingTrack = trackWithPointsFromDb.track

                val track = Track(
                    existingTrack.id,
                    existingTrack.name,
                    existingTrack.startTimeMillis,
                    System.currentTimeMillis(),
                    calculateDistanceFromPoints(trackWithPointsFromDb.points),
                    calculateAvgSpeedFromPoints(trackWithPointsFromDb.points),
                    calculateMaxSpeedFromPoints(trackWithPointsFromDb.points),
                    calculateOverallBearingFromPoints(trackWithPointsFromDb.points),
                    0, // TODO: places (when added to db)
                    trackWithPointsFromDb.points.size,
                    1 // TODO: status enum
                )

                GeoBreadcrumbsDatabase.getInstance(applicationContext)
                    .trackDao
                    .update(track)
                LogEx.d(Constants.TAG_GEO_TRACK_SERVICE, "update finished")
            }

            serviceScope.cancel()
            stopForeground(true)
        }
    }

    private fun calculateDistanceFromPoints(points: List<Point>): Float {
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

    private fun calculateAvgSpeedFromPointDtos(points: List<PointDto>): Float {
        if (points.isEmpty()) {
            return 0F
        }

        var result = 0F
        points.forEach { x -> result += x.speed }
        result /= points.size

        return result
    }

    // TODO: think of a way to unite there two methods!!
    private fun calculateAvgSpeedFromPoints(points: List<Point>): Float {
        if (points.isEmpty()) {
            return 0F
        }

        var result = 0F
        points.forEach { x -> result += x.speed }
        result /= points.size

        return result
    }

    private fun calculateMaxSpeedFromPointDtos(points: List<PointDto>): Float {
        var result = 0F
        points.forEach { x -> result = result.coerceAtLeast(x.speed) }

        return result
    }

    // TODO: think of a way to unite there two methods!!
    private fun calculateMaxSpeedFromPoints(points: List<Point>): Float {
        var result = 0F
        points.forEach { x -> result = result.coerceAtLeast(x.speed) }

        return result
    }

    private fun calculateOverallBearingFromPointDtos(points: List<PointDto>): Float {
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

    // TODO: think of a way to unite there two methods!!
    private fun calculateOverallBearingFromPoints(points: List<Point>): Float {
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

        // extras ----------------------------------------------------------------------------------

        const val EXTRA_START_POINT_NAME = "extra_start_point_name"
    }
}

// #################################################################################################

private class LocationSubscriptionParameters(val interval: Long, val distance: Float)
