package ns.fajnet.android.geobreadcrumbs.common

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

/**
 * Initialize global data/service channels
 */
class AppInit : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID_GEO_TRACK,
                Constants.NOTIFICATION_CHANNEL_NAME_GEO_TRACK,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )

            notificationManager.createNotificationChannel(serviceChannel)
        }
    }
}
