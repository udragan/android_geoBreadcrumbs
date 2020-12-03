package ns.fajnet.android.geobreadcrumbs.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(track: Track): Long

    @Transaction
    fun insertAndRetrieve(track: Track): Track {
        val id = insert(track)
        return get(id)
            ?: throw Exception("failed to insert track!") // TODO: think about this, should never happen
    }

    @Update
    fun update(track: Track)

    @Delete
    fun delete(track: Track)

    @Query("DELETE FROM tracks_table WHERE rowid = :id")
    fun deleteById(id: Int)

    @Query("DELETE FROM tracks_table")
    fun clear()

    @Query("SELECT * from tracks_table WHERE rowid = :key")
    fun get(key: Long): Track?

    // TODO: this returns null for some reason??
//    @Query("SELECT * from tracks_table WHERE rowid = :key")
//    fun getLive(key: Long): LiveData<Track?>

    fun getLive(key: Long): LiveData<Track?> {
        return MutableLiveData(get(key))
    }

    @Transaction
    @Query("SELECT * from tracks_table WHERE rowid = :key")
    fun getWithPoints(key: Long): TrackWithPoints?

    @Query("SELECT * from tracks_table ORDER BY rowid DESC")
    fun getAll(): Array<Track>

    @Transaction
    @Query("SELECT * from tracks_table ORDER BY rowid DESC")
    fun getAllWithPoints(): List<TrackWithPoints>
}
