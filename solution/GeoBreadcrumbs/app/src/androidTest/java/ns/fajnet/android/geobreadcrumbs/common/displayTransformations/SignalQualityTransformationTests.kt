package ns.fajnet.android.geobreadcrumbs.common.displayTransformations

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ns.fajnet.android.geobreadcrumbs.R
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignalQualityTransformationTests {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    // tests ---------------------------------------------------------------------------------------

    @Test
    fun should_transformToWarning() {
        // arrange
        val transformation = SignalQualityTransformation(context)
        val expected = ContextCompat.getColor(context, R.color.warning)
        // act
        val result = transformation.transform(2)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun should_TransformToTransparent() {
        // arrange
        val transformation = SignalQualityTransformation(context)
        val expected = ContextCompat.getColor(context, R.color.gray_text)
        // act
        val result = transformation.transform(25)
        // assert
        Assert.assertEquals(expected, result)
    }
}
