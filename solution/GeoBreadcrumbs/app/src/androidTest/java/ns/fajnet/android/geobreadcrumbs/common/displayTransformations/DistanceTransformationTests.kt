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
class DistanceTransformationTests {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val prefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit()

    // tests ---------------------------------------------------------------------------------------

    @Test
    fun should_transformDistanceToMeters() {
        // arrange
        ensureSelectedUnit("metric")
        val transformation = DistanceTransformation(context)
        val expected = "123 m"
        // act
        val result = transformation.transform(123F)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun should_transformDistanceToKilometers() {
        // arrange
        ensureSelectedUnit("metric")
        val transformation = DistanceTransformation(context)
        val expected = "1.2 km"
        // act
        val result = transformation.transform(1200F)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun should_transformDistanceToFeet() {
        // arrange
        ensureSelectedUnit("imperial")
        val transformation = DistanceTransformation(context)
        val expected = "328.08 ft"
        // act
        val result = transformation.transform(100F)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun should_transformDistanceToMiles() {
        // arrange
        ensureSelectedUnit("imperial")
        val transformation = DistanceTransformation(context)
        val expected = "1.24 miles"
        // act
        val result = transformation.transform(2000F)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun shouldNot_TransformAndReturnErrorMessage() {
        // arrange
        ensureSelectedUnit("notSupported")
        val transformation = DistanceTransformation(context)
        val expected = "Unit not supported: notSupported"
        // act
        val result = transformation.transform(100F)
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
