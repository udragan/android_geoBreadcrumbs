package ns.fajnet.android.geobreadcrumbs.dtos

import ns.fajnet.android.geobreadcrumbs.database.Track

data class TrackDto(
    val id: Long = 0L,
    val name: String = "",
    val startTimeMillis: Long = 0L,
    val duration: Long = 0L,
    val distance: Float = 0F,
    val currentSpeed: Float = 0F,
    val averageSpeed: Float = 0F,
    val maxSpeed: Float = 0F,
    val currentBearing: Float = 0F,
    val overallBearing: Float = 0F,
    val noOfPlaces: Int = 0,
    val noOfPoints: Int = 0
) {

    // companion object ----------------------------------------------------------------------------

    companion object {
        fun fromDb(track: Track): TrackDto {
            return TrackDto(
                track.id,
                track.name,
                track.startTimeMillis,
                track.endTimeMillis - track.startTimeMillis,
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
