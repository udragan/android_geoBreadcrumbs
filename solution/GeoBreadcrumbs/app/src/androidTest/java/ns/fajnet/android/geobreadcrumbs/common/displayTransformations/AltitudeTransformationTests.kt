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
class AltitudeTransformationTests {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val prefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit()

    // tests ---------------------------------------------------------------------------------------

    @Test
    fun should_transformAltitudeToMeters() {
        // arrange
        ensureSelectedUnit("metric")
        val transformation = AltitudeTransformation(context)
        val expected = "42 m"
        // act
        val result = transformation.transform(42.0)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun should_transformAltitudeToFeet() {
        // arrange
        ensureSelectedUnit("imperial")
        val transformation = AltitudeTransformation(context)
        val expected = "138 ft"
        // act
        val result = transformation.transform(42.0)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun shouldNot_TransformAndReturnErrorMessage() {
        // arrange
        ensureSelectedUnit("notSupported")
        val transformation = AltitudeTransformation(context)
        val expected = "Unit not supported: notSupported"
        // act
        val result = transformation.transform(100.0)
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
