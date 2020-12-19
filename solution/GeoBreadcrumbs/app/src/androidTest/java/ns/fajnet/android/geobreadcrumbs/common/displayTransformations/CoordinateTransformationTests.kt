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
class CoordinateTransformationTests {


    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val prefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit()

    // tests ---------------------------------------------------------------------------------------

    @Test
    fun should_transformLongitudeToDecimalDegrees() {
        // arrange
        ensureSelectedUnit("decimal")
        val transformation = CoordinateTransformation(context)
        val expected = "10.23456º"
        // act
        val result = transformation.transform(10.23456, Orientation.HORIZONTAL)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun should_transformLatitudeToDecimalDegrees() {
        // arrange
        ensureSelectedUnit("decimal")
        val transformation = CoordinateTransformation(context)
        val expected = "10.23456º"
        // act
        val result = transformation.transform(10.23456, Orientation.VERTICAL)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun should_transformLongitudeToDegMinSecEast() {
        // arrange
        ensureSelectedUnit("degrees_minutes_seconds")
        val transformation = CoordinateTransformation(context)
        val expected = "10º 7\" 24.24' E"
        // act
        val result = transformation.transform(10.1234, Orientation.HORIZONTAL)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun should_transformLongitudeToDegMinSecWest() {
        // arrange
        ensureSelectedUnit("degrees_minutes_seconds")
        val transformation = CoordinateTransformation(context)
        val expected = "10º 7\" 24.24' W"
        // act
        val result = transformation.transform(-10.1234, Orientation.HORIZONTAL)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun should_transformLongitudeToDegMinSecNorth() {
        // arrange
        ensureSelectedUnit("degrees_minutes_seconds")
        val transformation = CoordinateTransformation(context)
        val expected = "10º 7\" 24.24' N"
        // act
        val result = transformation.transform(10.1234, Orientation.VERTICAL)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun should_transformLongitudeToDegMinSecSouth() {
        // arrange
        ensureSelectedUnit("degrees_minutes_seconds")
        val transformation = CoordinateTransformation(context)
        val expected = "10º 7\" 24.24' S"
        // act
        val result = transformation.transform(-10.1234, Orientation.VERTICAL)
        // assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun shouldNot_TransformAndReturnErrorMessage() {
        // arrange
        ensureSelectedUnit("notSupported")
        val transformation = CoordinateTransformation(context)
        val expected = "Unit not supported: notSupported"
        // act
        val result = transformation.transform(10.0, Orientation.HORIZONTAL)
        // assert
        Assert.assertEquals(expected, result)
    }

    // private methods -----------------------------------------------------------------------------

    private fun ensureSelectedUnit(unit: String) {
        prefEditor.putString(
            context.getString(R.string.settings_preference_unit_coordinate_key),
            unit
        )
        prefEditor.commit()
    }
}
