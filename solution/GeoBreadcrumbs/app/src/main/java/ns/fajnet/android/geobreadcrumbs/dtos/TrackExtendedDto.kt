package ns.fajnet.android.geobreadcrumbs.dtos

import ns.fajnet.android.geobreadcrumbs.database.TrackExtended

data class TrackExtendedDto(
    val track: TrackDto,
    val places: List<PlaceDto>,
    val points: List<PointDto>
) {

    // companion -----------------------------------------------------------------------------------

    companion object {
        fun default() = TrackExtendedDto(TrackDto(), listOf(), listOf())

        fun fromDb(value: TrackExtended) = TrackExtendedDto(
                TrackDto.fromDb(value.track),
                value.places.map { PlaceDto.fromDb(it) },
                value.points.map { PointDto.fromDb(it) }
            )
    }
}
