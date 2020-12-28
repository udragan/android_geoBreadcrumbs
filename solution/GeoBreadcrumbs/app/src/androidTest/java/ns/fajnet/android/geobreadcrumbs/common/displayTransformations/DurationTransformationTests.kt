package ns.fajnet.android.geobreadcrumbs.common.displayTransformations

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DurationTransformationTests {

    // tests ---------------------------------------------------------------------------------------

    @Test
    fun should_transformMillisecondsToStringFormatSecond() {
        // arrange
        val transformation = DurationTransformation()
        val expected = "0:00:01"
        // act
        val result = transformation.transform(1000)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun should_transformMillisecondsToStringFormatHMS() {
        // arrange
        val transformation = DurationTransformation()
        val expected = "12:34:56"
        // act
        val result = transformation.transform(45296000)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun shouldNot_TransformAndReturnErrorMessage() {
        // arrange
        val transformation = DurationTransformation()
        val expected = "Negative duration: -100"
        // act
        val result = transformation.transform(-100)
        // assert
        Assert.assertEquals(expected, result)
    }
}
