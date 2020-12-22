package ns.fajnet.android.geobreadcrumbs.common

internal class Constants {
    companion object {

        // request codes ---------------------------------------------------------------------------

        const val REQUEST_CODE_REQUEST_LOCATION_PERMISSION = 1000

        // notification channels -------------------------------------------------------------------

        const val NOTIFICATION_CHANNEL_ID_GEO_TRACK = "geoTrackNotificationChannel"
        const val NOTIFICATION_CHANNEL_NAME_GEO_TRACK = "GEO Track Channel"

        // service ids -----------------------------------------------------------------------------

        const val SERVICE_ID_GEO_TRACK = 1000

        // logger tags -----------------------------------------------------------------------------

        const val TAG_MAIN_ACTIVITY = "appTag_main"
        const val TAG_MAIN_ACTIVITY_VM = "appTag_mainVM"
        const val TAG_LIVE_GPS_FRAGMENT = "appTag_liveGPS"
        const val TAG_LIVE_GPS_FRAGMENT_VM = "appTag_liveGPSVM"
        const val TAG_CURRENT_TRACK_FRAGMENT = "appTag_currentTrack"
        const val TAG_CURRENT_TRACK_FRAGMENT_VM = "appTag_currentTrackVM"
        const val TAG_RECORDED_TRACKS_FRAGMENT = "appTag_recordedTracks"
        const val TAG_RECORDED_TRACKS_FRAGMENT_VM = "appTag_recordedTracksVM"

        const val TAG_GEO_TRACK_SERVICE = "appTag_service"

        const val TAG_TRANSFORMATION_COORDINATE = "appTag_transformation_coordinate"
        const val TAG_TRANSFORMATION_ALTITUDE = "appTag_transformation_altitude"
        const val TAG_TRANSFORMATION_DISTANCE = "appTag_transformation_distance"
        const val TAG_TRANSFORMATION_SPEED = "appTag_transformation_speed"
    }
}
