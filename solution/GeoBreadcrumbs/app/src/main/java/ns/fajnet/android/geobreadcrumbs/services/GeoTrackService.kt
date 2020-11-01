package ns.fajnet.android.geobreadcrumbs.services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.activities.main.MainActivity
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.Utils

class GeoTrackService : Service() {

    // members -------------------------------------------------------------------------------------

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


    }

    // private methods -----------------------------------------------------------------------------

    private fun initialize() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                Log.d(Constants.TAG_LIVE_GPS_FRAGMENT, "locationCallbackTriggered")

                for (location in locationResult.locations) {
                    Log.d(Constants.TAG_GEO_TRACK_SERVICE, "location received: $location")
                    // TODO: persist data to current track
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
        // TODO: create locationRequest form parameters set in preferences (extract method)
        val locationRequest = LocationRequest().setInterval(5000)
            .setFastestInterval(5000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
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
}
