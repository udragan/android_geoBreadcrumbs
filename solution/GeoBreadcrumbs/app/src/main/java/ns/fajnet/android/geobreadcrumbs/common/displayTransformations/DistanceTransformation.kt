package ns.fajnet.android.geobreadcrumbs.common.displayTransformations

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.logger.LogEx
import java.math.RoundingMode
import java.text.DecimalFormat

class DistanceTransformation(val context: Context) :
    SharedPreferences.OnSharedPreferenceChangeListener {

    // members -------------------------------------------------------------------------------------

    private var unit = ""
    private val decimalFormat =
        DecimalFormat("#,###.##").apply { roundingMode = RoundingMode.HALF_EVEN }
    private val feetInMeter = 3.2808
    private val feetInMile = 5280

    // init ----------------------------------------------------------------------------------------

    init {
        readExistingPreference()
        registerPreferenceChangeListener()
    }

    // OnSharedPreferencesChangedListener ----------------------------------------------------------

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == context.getString(R.string.settings_preference_unit_measurement_key)) {
            LogEx.d(Constants.TAG_TRANSFORMATION_DISTANCE, "preference changed")
            val defaultValue = context.resources.getStringArray(R.array.unit_measurement_values)[0]
            unit = sharedPreferences
                .getString(
                    context.getString(R.string.settings_preference_unit_measurement_key),
                    defaultValue
                )!!
        }
    }

    // public methods ------------------------------------------------------------------------------

    fun transform(distance: Float): String {
        val unitSystem = context.resources.getStringArray(R.array.unit_measurement_values)

        return when (unit) {
            unitSystem[0] -> {
                if (distance > 1000) {
                    return "${decimalFormat.format(distance / 1000)} km"
                }

                "${decimalFormat.format(distance)} m"
            }
            unitSystem[1] -> {
                val distanceInFeet = distance * feetInMeter

                if (distanceInFeet > feetInMile) {
                    return "${decimalFormat.format(distanceInFeet / feetInMile)} miles"
                }

                "${decimalFormat.format(distanceInFeet)} ft"
            }
            else -> {
                "Unit not supported: $unit"
            }
        }
    }

    // private methods -----------------------------------------------------------------------------

    private fun readExistingPreference() {
        val defaultValue =
            context.resources.getStringArray(R.array.unit_measurement_values)[0]
        unit = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(
                context.getString(R.string.settings_preference_unit_measurement_key),
                defaultValue
            )!!
    }

    private fun registerPreferenceChangeListener() {
        LogEx.d(Constants.TAG_TRANSFORMATION_DISTANCE, "register preference change listener")
        PreferenceManager.getDefaultSharedPreferences(context)
            .registerOnSharedPreferenceChangeListener(this)
    }

    // CHECK: should it be unregistered? if so then how?
    private fun unregisterPreferenceChangeListener() {
        LogEx.d(Constants.TAG_TRANSFORMATION_DISTANCE, "unregister preference change listener")
        PreferenceManager.getDefaultSharedPreferences(context)
            .unregisterOnSharedPreferenceChangeListener(this)
    }
}
