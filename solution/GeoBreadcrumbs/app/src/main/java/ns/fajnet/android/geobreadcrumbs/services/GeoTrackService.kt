package ns.fajnet.android.geobreadcrumbs.services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.activities.main.MainActivity
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.Utils

class GeoTrackService : Service() {

    // members -------------------------------------------------------------------------------------

    private lateinit var handleLocationUpdatesJob: Job
    private lateinit var serviceScope: CoroutineScope
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    // overrides -----------------------------------------------------------------------------------

    override fun onCreate() {
        super.onCreate()
        Log.d(Constants.TAG_GEO_TRACK_SERVICE, "onCreate")
        initialize()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(Constants.TAG_GEO_TRACK_SERVICE, "onStartCommand")
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification =
            NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID_GEO_TRACK)
                .setContentTitle(getString(R.string.geo_track_service_notification_title))
                .setContentText(getString(R.string.geo_track_service_notification_text))
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .setContentIntent(pendingIntent)
                .build()

        startForeground(Constants.SERVICE_ID_GEO_TRACK, notification)

        if (checkPrerequisites()) {
            subscribeToLocationUpdates()
        } else {
            stopSelf()
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO("Return the communication channel to the service.")
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(Constants.TAG_GEO_TRACK_SERVICE, "onDestroy")

        unsubscribeFromLocationUpdates()
        serviceScope.cancel()
    }

    // private methods -----------------------------------------------------------------------------

    private fun initialize() {
        handleLocationUpdatesJob = Job()
        // CHECK: is Dispatchers.Main the correct one for this CoroutineScope??
        serviceScope = CoroutineScope(Dispatchers.Main + handleLocationUpdatesJob)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                Log.d(Constants.TAG_GEO_TRACK_SERVICE, "locationCallbackTriggered")

                serviceScope.launch {
                    withContext(Dispatchers.IO) {
                        super.onLocationResult(locationResult)

                        for (location in locationResult.locations) {
                            Log.d(Constants.TAG_GEO_TRACK_SERVICE, "location received: $location")
                            // TODO: persist data to current track
                            delay(5000)
                            Log.d(Constants.TAG_GEO_TRACK_SERVICE, "location persisted")
                        }
                    }
                }
            }
        }
    }

    private fun checkPrerequisites(): Boolean {
        Log.d(
            Constants.TAG_GEO_TRACK_SERVICE,
            "hasPermission: ${Utils.isPermissionGranted(this)}, locationEnabled: ${Utils.isLocationEnabled(
                this
            )}"
        )
        return Utils.isPermissionGranted(this) && Utils.isLocationEnabled(this)
    }

    @SuppressLint("MissingPermission")
    private fun subscribeToLocationUpdates() {
        Log.d(Constants.TAG_GEO_TRACK_SERVICE, "subscribe to location updates")

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(
            generateLocationRequest(),
            locationCallback,
            Looper.myLooper()
        )
    }

    private fun unsubscribeFromLocationUpdates() {

        if (this::fusedLocationProviderClient.isInitialized) {
            Log.d(Constants.TAG_GEO_TRACK_SERVICE, "unsubscribe from location updates")
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
}

// #################################################################################################

private class LocationSubscriptionParameters(val interval: Long, val distance: Float)
