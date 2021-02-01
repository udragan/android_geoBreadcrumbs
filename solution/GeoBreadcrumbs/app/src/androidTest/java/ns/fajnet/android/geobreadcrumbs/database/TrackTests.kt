package ns.fajnet.android.geobreadcrumbs.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TrackTests {

    private lateinit var db: GeoBreadcrumbsDatabase
    private lateinit var trackDao: TrackDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, GeoBreadcrumbsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        trackDao = db.trackDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    // Tests ---------------------------------------------------------------------------------------

    @Test
    @Throws(Exception::class)
    fun should_writeAndReadSingleTrack() {
        // arrange
        val track = Track(1)
        // act
        trackDao.insert(track)
        val result = trackDao.get(1)
        // assert
        assertEquals(track, result)
    }

    @Test
    @Throws(Exception::class)
    fun should_writeAndReadSingleLiveTrack() {
        // arrange
        val track = Track(1)
        // act
        trackDao.insert(track)
        val result = trackDao.getLive(1)
        // assert
        assertEquals(track, result.value)
    }

    @Test
    @Throws(Exception::class)
    fun should_writeAndUpdateSingleTrack() {
        // arrange
        val track = Track(1)
        val updatedTrack = Track(1, status = 1)
        // act
        trackDao.insert(track)
        trackDao.update(updatedTrack)
        val result = trackDao.get(1)
        // assert
        assertEquals(updatedTrack, result)
    }

    @Test
    @Throws(Exception::class)
    fun should_writeAndDeleteSingleTrack() {
        // arrange
        val track = Track(1)
        val track2 = Track(2)
        val expectedTracksCount = 1
        // act
        trackDao.insert(track)
        trackDao.insert(track2)
        trackDao.delete(track)
        val result = trackDao.getAll()
        // assert
        assertEquals(expectedTracksCount, result.count())
    }

    @Test
    @Throws(Exception::class)
    fun should_writeAndDeleteSelectedTracks() {
        // arrange
        val track = Track(1)
        val track2 = Track(2)
        val track3 = Track(3)
        val expectedTracksCount = 1
        // act
        trackDao.insert(track)
        trackDao.insert(track2)
        trackDao.insert(track3)
        trackDao.deleteMultiple(arrayListOf(track.id, track2.id))
        val result = trackDao.getAll()
        // assert
        assertEquals(expectedTracksCount, result.count())
    }

    @Test
    @Throws(Exception::class)
    fun should_writeAndDeleteAllTracks() {
        // arrange
        val track = Track(1)
        val track2 = Track(2)
        val expectedTracksCount = 0
        // act
        trackDao.insert(track)
        trackDao.insert(track2)
        trackDao.clear()
        val result = trackDao.getAll()
        // assert
        assertEquals(expectedTracksCount, result.count())
    }
}
