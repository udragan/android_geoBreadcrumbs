package ns.fajnet.android.geobreadcrumbs.common

internal class Constants {
    companion object {
        const val REQUEST_CODE_REQUEST_LOCATION_PERMISSION = 1000

        // notification channels -------------------------------------------------------------------

        const val NOTIFICATION_CHANNEL_ID_GEO_TRACK = "geoTrackNotificationChannel"
        const val NOTIFICATION_CHANNEL_NAME_GEO_TRACK = "GEO Track Channel"

        // service ids -----------------------------------------------------------------------------

        const val SERVICE_ID_GEO_TRACK = 1000

        // logger tags -----------------------------------------------------------------------------

        const val TAG_GEO_TRACK_SERVICE = "appTag_service"
    }
}