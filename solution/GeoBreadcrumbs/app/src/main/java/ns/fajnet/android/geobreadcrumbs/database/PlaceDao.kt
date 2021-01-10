package ns.fajnet.android.geobreadcrumbs.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Dao
interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(point: Place)

    @Update
    fun update(point: Place)

    @Delete
    fun delete(point: Place)

    @Query("DELETE FROM places_table WHERE rowid = :id")
    fun deleteById(id: Int)

    @Query("DELETE FROM places_table")
    fun clear()

    @Query("SELECT * from places_table WHERE rowid = :key")
    fun get(key: Long): Place?

    // TODO: this returns null for some reason??
//    @Query("SELECT * from places_table WHERE rowid = :key")
//    fun getLive(key: Long): LiveData<Point?>

    fun getLive(key: Long): LiveData<Place?> {
        return MutableLiveData(get(key))
    }

    @Query("SELECT * from places_table ORDER BY rowid DESC")
    fun getAll(): List<Place>

    @Query("SELECT * from places_table WHERE track_id = :trackKey")
    fun getAllForTrack(trackKey: Long): List<Place>
}
