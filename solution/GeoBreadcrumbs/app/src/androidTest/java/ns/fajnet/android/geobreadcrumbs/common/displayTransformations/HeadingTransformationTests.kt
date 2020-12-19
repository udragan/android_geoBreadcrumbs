package ns.fajnet.android.geobreadcrumbs.common.displayTransformations

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HeadingTransformationTests {

    // tests ---------------------------------------------------------------------------------------

    @Test
    fun should_transformHeadingToNorth() {
        // arrange
        val transformation = HeadingTransformation()
        val expected = "N"
        // act
        val result = transformation.transform(-10F)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun should_transformHeadingToNorthEast() {
        // arrange
        val transformation = HeadingTransformation()
        val expected = "NE"
        // act
        val result = transformation.transform(40F)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun should_transformHeadingToEast() {
        // arrange
        val transformation = HeadingTransformation()
        val expected = "E"
        // act
        val result = transformation.transform(100F)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun should_transformHeadingToSouthEast() {
        // arrange
        val transformation = HeadingTransformation()
        val expected = "SE"
        // act
        val result = transformation.transform(140F)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun should_transformHeadingToSouth() {
        // arrange
        val transformation = HeadingTransformation()
        val expected = "S"
        // act
        val result = transformation.transform(170F)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun should_transformHeadingToSouthWest() {
        // arrange
        val transformation = HeadingTransformation()
        val expected = "SW"
        // act
        val result = transformation.transform(-140F)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun should_transformHeadingToWest() {
        // arrange
        val transformation = HeadingTransformation()
        val expected = "W"
        // act
        val result = transformation.transform(-80F)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun should_transformHeadingToNorthWest() {
        // arrange
        val transformation = HeadingTransformation()
        val expected = "NW"
        // act
        val result = transformation.transform(-40F)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun shouldNot_TransformAndReturnErrorMessage() {
        // arrange
        val transformation = HeadingTransformation()
        val expected = "invalid heading"
        // act
        val result = transformation.transform(200F)
        // assert
        Assert.assertEquals(expected, result)
    }
}
