package ns.fajnet.android.geobreadcrumbs.common.displayTransformations

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccuracyTransformationTests {

    // tests ---------------------------------------------------------------------------------------

    @Test
    fun should_transformAccuracyToRadiusInMeters() {
        // arrange
        val transformation = AccuracyTransformation()
        val expected = "20.0 m"
        // act
        val result = transformation.transform(20F)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun shouldNot_TransformAndReturnErrorMessage() {
        // arrange
        val transformation = AccuracyTransformation()
        val expected = "invalid accuracy: -20.0"
        // act
        val result = transformation.transform(-20F)
        // assert
        Assert.assertEquals(expected, result)
    }
}
