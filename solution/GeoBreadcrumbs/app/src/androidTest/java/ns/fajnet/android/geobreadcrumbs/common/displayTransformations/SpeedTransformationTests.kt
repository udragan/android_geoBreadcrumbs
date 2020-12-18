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
class SpeedTransformationTests {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val prefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit()

    // tests ---------------------------------------------------------------------------------------

    @Test
    fun should_transformSpeedToMetersPerSecond() {
        // arrange
        ensureSelectedUnit("ms")
        val transformation = SpeedTransformation(context)
        val expected = "123 m/s"
        // act
        val result = transformation.transform(123F)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun should_transformSpeedToKilometersPerHour() {
        // arrange
        ensureSelectedUnit("kmh")
        val transformation = SpeedTransformation(context)
        val expected = "36 km/h"
        // act
        val result = transformation.transform(10F)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun should_transformSpeedToMilesPerHour() {
        // arrange
        ensureSelectedUnit("mph")
        val transformation = SpeedTransformation(context)
        val expected = "22.3 mph"
        // act
        val result = transformation.transform(10F)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun shouldNot_TransformAndReturnErrorMessage() {
        // arrange
        ensureSelectedUnit("notSupported")
        val transformation = SpeedTransformation(context)
        val expected = "Unit not supported: notSupported"
        // act
        val result = transformation.transform(100F)
        // assert
        Assert.assertEquals(expected, result)
    }

    // private methods -----------------------------------------------------------------------------

    private fun ensureSelectedUnit(unit: String) {
        prefEditor.putString(
            context.getString(R.string.settings_preference_speed_unit_key),
            unit
        )
        prefEditor.commit()
    }
}
