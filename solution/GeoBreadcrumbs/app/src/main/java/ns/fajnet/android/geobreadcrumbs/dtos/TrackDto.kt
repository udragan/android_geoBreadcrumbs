package ns.fajnet.android.geobreadcrumbs.dtos

import ns.fajnet.android.geobreadcrumbs.database.Track

data class TrackDto(
    var id: Long = 0L,
    var duration: String = "",
    var distance: Float = 0F,
    var currentSpeed: Float = 0F,
    var averageSpeed: Float = 0F,
    var maxSpeed: Float = 0F,
    var currentBearing: Float = 0F,
    var overallBearing: Float = 0F,
    var noOfPlaces: Int = 0,
    var noOfPoints: Int = 0
) {

    // companion object ----------------------------------------------------------------------------

    companion object {
        fun fromTrack(track: Track): TrackDto {
            return TrackDto(
                track.id,
                "0",
                track.distance,
                0F,
                track.averageSpeed,
                track.maxSpeed,
                0F,
                track.bearing,
                track.numberOfPlaces,
                track.numberOfPoints
            )
        }
    }
}
