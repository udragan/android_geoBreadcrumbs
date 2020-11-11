package ns.fajnet.android.geobreadcrumbs.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Dao
interface PointDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(point: Point)

    @Update
    fun update(point: Point)

    @Delete
    fun delete(point: Point)

    @Query("DELETE FROM points_table WHERE rowid = :id")
    fun deleteById(id: Int)

    @Query("DELETE FROM points_table")
    fun clear()

    @Query("SELECT * from points_table WHERE rowid = :key")
    fun get(key: Long): Point?

    // TODO: this returns null for some reason??
//    @Query("SELECT * from tracks_table WHERE rowid = :key")
//    fun getLive(key: Long): LiveData<Point?>

    fun getLive(key: Long): LiveData<Point?> {
        return MutableLiveData(get(key))
    }

    @Query("SELECT * from points_table ORDER BY rowid DESC")
    fun getAll(): List<Point>

    @Query("SELECT * from points_table WHERE track_id = :trackKey")
    fun getAllForTrack(trackKey: Long): List<Point>
}
