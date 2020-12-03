package ns.fajnet.android.geobreadcrumbs.dtos

data class TrackExtendedDto(
    var track: TrackDto,
    var points: MutableList<PointDto>
)
