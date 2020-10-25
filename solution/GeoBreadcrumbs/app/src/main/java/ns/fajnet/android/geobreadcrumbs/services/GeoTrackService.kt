package ns.fajnet.android.geobreadcrumbs.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.activities.main.MainActivity
import ns.fajnet.android.geobreadcrumbs.common.Constants

class GeoTrackService : Service() {

    // overrides -----------------------------------------------------------------------------------

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(Constants.TAG_GEO_TRACK_SERVICE, "onStartCommand called")

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

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO("Return the communication channel to the service.")
        return null
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(Constants.TAG_GEO_TRACK_SERVICE, "onDestroy called")

    }
}
