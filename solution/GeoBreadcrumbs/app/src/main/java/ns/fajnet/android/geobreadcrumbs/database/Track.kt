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

    @ColumnInfo(name = "distance")
    val distance: Float = 0F,

    @ColumnInfo(name = "average_speed")
    val averageSpeed: Float = 0F,

    @ColumnInfo(name = "max_speed")
    val maxSpeed: Float = 0F,

    @ColumnInfo(name = "bearing")
    val bearing: Float = 0F,

    @ColumnInfo(name = "no_of_places")
    val numberOfPlaces: Int = 0,

    @ColumnInfo(name = "no_of_points")
    val numberOfPoints: Int = 0,

    @ColumnInfo(name = "status")
    val status: Int = -1
)
