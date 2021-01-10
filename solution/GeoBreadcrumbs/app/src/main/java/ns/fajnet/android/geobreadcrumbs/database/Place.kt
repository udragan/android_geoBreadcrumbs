package ns.fajnet.android.geobreadcrumbs.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "places_table",
    foreignKeys = [ForeignKey(
        entity = Track::class,
        parentColumns = ["rowid"],
        childColumns = ["track_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Place(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val id: Long = 0L,

    @ColumnInfo(name = "track_id")
    val trackId: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "longitude")
    val longitude: Double = 0.0,

    @ColumnInfo(name = "latitude")
    val latitude: Double = 0.0,

    @ColumnInfo(name = "altitude")
    val altitude: Double = 0.0,

    @ColumnInfo(name = "location_fix_time")
    val locationFixTime: Long = 0L
)
