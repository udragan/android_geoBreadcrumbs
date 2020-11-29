package ns.fajnet.android.geobreadcrumbs.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "points_table",
    foreignKeys = [ForeignKey(
        entity = Track::class,
        parentColumns = ["rowid"],
        childColumns = ["track_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Point(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val id: Long = 0L,

    @ColumnInfo(name = "track_id")
    val trackId: Long = 0L,

    @ColumnInfo(name = "longitude")
    val longitude: Double = 0.0,

    @ColumnInfo(name = "latitude")
    val latitude: Double = 0.0,

    @ColumnInfo(name = "altitude")
    val altitude: Double = 0.0,

    @ColumnInfo(name = "accuracy")
    val accuracy: Float = 0F,

    @ColumnInfo(name = "speed")
    val speed: Float = 0F,

    @ColumnInfo(name = "bearing")
    val bearing: Float = 0F,

    @ColumnInfo(name = "location_fix_time")
    val locationFixTime: Long = 0L
)
