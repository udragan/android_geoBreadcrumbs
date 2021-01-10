package ns.fajnet.android.geobreadcrumbs.dtos

data class TrackExtendedDto(
    var track: TrackDto,
    var places: MutableList<PlaceDto>,
    var points: MutableList<PointDto>
) {

    // companion -----------------------------------------------------------------------------------

    companion object {
        fun default(): TrackExtendedDto {
            return TrackExtendedDto(TrackDto(), mutableListOf(), mutableListOf())
        }
    }
}
