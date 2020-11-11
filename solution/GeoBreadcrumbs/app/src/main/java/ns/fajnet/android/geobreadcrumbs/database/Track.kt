package ns.fajnet.android.geobreadcrumbs.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks_table")
data class Track(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    var id: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "start_time_millis")
    val startTimeMillis: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "end_time_millis")
    val endTimeMillis: Long = startTimeMillis,

    @ColumnInfo(name = "no_of_places")
    val numberOfPlaces: Int = 0, // TODO: think about this, is it needed??

    @ColumnInfo(name = "no_of_points")
    val numberOfPoints: Int = 0, // TODO: think about this, is it needed??

    @ColumnInfo(name = "status")
    val status: Int = -1
)
