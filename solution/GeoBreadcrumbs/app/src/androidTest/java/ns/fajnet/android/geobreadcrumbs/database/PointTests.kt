package ns.fajnet.android.geobreadcrumbs.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PointTests {

    private lateinit var db: GeoBreadcrumbsDatabase
    private lateinit var pointDao: PointDao
    private lateinit var trackDao: TrackDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, GeoBreadcrumbsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        pointDao = db.pointDao
        trackDao = db.trackDao
        trackDao.insert(Track())
    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        db.close()
    }

    // Tests ---------------------------------------------------------------------------------------

    @Test
    fun should_writeAndReadSinglePoint() {
        // arrange
        val point = Point(1, trackId = 1)
        // act
        pointDao.insert(point)
        val result = pointDao.get(1)
        // assert
        Assert.assertEquals(point, result)
    }

    @Test
    fun should_writeAndReadSingleLivePoint() {
        // arrange
        val point = Point(1, trackId = 1)
        // act
        pointDao.insert(point)
        val result = pointDao.getLive(1)
        // assert
        Assert.assertEquals(point, result.value)
    }

    @Test
    @Throws(Exception::class)
    fun should_writeAndUpdateSinglePoint() {
        // arrange
        val point = Point(1, trackId = 1)
        val updatedPoint = Point(1, trackId = 1, accuracy = 10F)
        // act
        pointDao.insert(point)
        pointDao.update(updatedPoint)
        val result = pointDao.get(1)
        // assert
        Assert.assertEquals(updatedPoint, result)
    }

    @Test
    @Throws(Exception::class)
    fun should_writeAndDeleteSinglePoint() {
        // arrange
        val point1 = Point(1, trackId = 1)
        val point2 = Point(2, trackId = 1)
        val expectedPointsCount = 1
        // act
        pointDao.insert(point1)
        pointDao.insert(point2)
        pointDao.delete(point1)
        val result = pointDao.getAll()
        // assert
        Assert.assertEquals(expectedPointsCount, result.count())
    }

    @Test
    @Throws(Exception::class)
    fun should_writeAndDeleteAllPoints() {
        // arrange
        val point1 = Point(1, trackId = 1)
        val point2 = Point(2, trackId = 1)
        val expectedPointsCount = 0
        // act
        pointDao.insert(point1)
        pointDao.insert(point2)
        pointDao.clear()
        val result = pointDao.getAll()
        // assert
        Assert.assertEquals(expectedPointsCount, result.count())
    }

    @Test
    @Throws(Exception::class)
    fun should_writeAndDeleteAllPointsRelatedToTrack() {
        // arrange
        val track1 = Track(1)
        val track2 = Track(2)
        val point1 = Point(1, trackId = 1)
        val point2 = Point(2, trackId = 1)
        val point3 = Point(3, trackId = 2)
        val expectedResult = 1
        // act
        trackDao.insert(track1)
        trackDao.insert(track2)
        pointDao.insert(point1)
        pointDao.insert(point2)
        pointDao.insert(point3)
        trackDao.delete(track1)
        val result = pointDao.getAll()
        // assert
        Assert.assertEquals(expectedResult, result.count())
    }

    @Test
    @Throws(Exception::class)
    fun should_writeAndGetAllPointsRelatedToTrack() {
        // arrange
        val track1 = Track(1)
        val track2 = Track(2)
        val point1 = Point(1, trackId = 1)
        val point2 = Point(2, trackId = 1)
        val point3 = Point(3, trackId = 2)
        val expectedResult = 2
        // act
        trackDao.insert(track1)
        trackDao.insert(track2)
        pointDao.insert(point1)
        pointDao.insert(point2)
        pointDao.insert(point3)
        val result = pointDao.getAllForTrack(1)
        // assert
        Assert.assertEquals(expectedResult, result.count())
    }
}
