package ns.fajnet.android.geobreadcrumbs.dtos

import ns.fajnet.android.geobreadcrumbs.database.Point

data class PointDto(
    var id: Long,
    var trackId: Long,
    var longitude: Double,
    var latitude: Double,
    var altitude: Double,
    var accuracy: Float, // ??
    var speed: Float,
    var bearing: Float,
    var locationFixTime: Long
) {

    // companion object ----------------------------------------------------------------------------

    companion object {
        fun fromDb(point: Point) = PointDto(
                point.id,
                point.trackId,
                point.longitude,
                point.latitude,
                point.altitude,
                point.accuracy,
                point.speed,
                point.bearing,
                point.locationFixTime
            )
    }
}
