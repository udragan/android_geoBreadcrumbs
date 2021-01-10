package ns.fajnet.android.geobreadcrumbs.dtos

import ns.fajnet.android.geobreadcrumbs.database.Place

data class PlaceDto(
    var id: Long,
    var trackId: Long,
    var name: String,
    var longitude: Double,
    var latitude: Double,
    var altitude: Double,
    var locationFixTime: Long
) {

    // companion object ----------------------------------------------------------------------------

    companion object {
        fun fromPlace(place: Place): PlaceDto {
            return PlaceDto(
                place.id,
                place.trackId,
                place.name,
                place.longitude,
                place.latitude,
                place.altitude,
                place.locationFixTime
            )
        }
    }
}
