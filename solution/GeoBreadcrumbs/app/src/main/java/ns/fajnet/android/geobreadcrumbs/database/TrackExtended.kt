package ns.fajnet.android.geobreadcrumbs.database

import androidx.room.Embedded
import androidx.room.Relation

data class TrackExtended(
    @Embedded val track: Track,
    @Relation(
        parentColumn = "rowid",
        entityColumn = "track_id"
    ) val places: List<Place>,
    @Relation(
        parentColumn = "rowid",
        entityColumn = "track_id"
    ) val points: List<Point>
)
