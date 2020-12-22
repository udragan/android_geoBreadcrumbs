package ns.fajnet.android.geobreadcrumbs.common.displayTransformations

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ns.fajnet.android.geobreadcrumbs.R
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccuracyTransformationTests {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val prefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit()

    // tests ---------------------------------------------------------------------------------------

    @Test
    fun should_transformAccuracyToRadiusInMeters() {
        // arrange
        ensureSelectedUnit("metric")
        val transformation = AccuracyTransformation(context)
        val expected = "20 m"
        // act
        val result = transformation.transform(20F)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun should_transformAccuracyToRadiusInFeet() {
        // arrange
        ensureSelectedUnit("imperial")
        val transformation = AccuracyTransformation(context)
        val expected = "66 ft"
        // act
        val result = transformation.transform(20F)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun shouldNot_TransformAndReturnErrorMessageInvalidAccuracy() {
        // arrange
        val transformation = AccuracyTransformation(context)
        val expected = "invalid accuracy: -20.0"
        // act
        val result = transformation.transform(-20F)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun shouldNot_TransformAndReturnErrorMessageUnsupportedUnit() {
        // arrange
        ensureSelectedUnit("notSupported")
        val transformation = AccuracyTransformation(context)
        val expected = "Unit not supported: notSupported"
        // act
        val result = transformation.transform(20F)
        // assert
        Assert.assertEquals(expected, result)
    }

    // private methods -----------------------------------------------------------------------------

    private fun ensureSelectedUnit(unit: String) {
        prefEditor.putString(
            context.getString(R.string.settings_preference_unit_measurement_key),
            unit
        )
        prefEditor.commit()
    }
}
