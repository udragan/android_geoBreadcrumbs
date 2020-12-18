package ns.fajnet.android.geobreadcrumbs.common.displayTransformations

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.logger.LogEx
import java.math.RoundingMode
import java.text.DecimalFormat

class SpeedTransformation(val context: Context) :
    SharedPreferences.OnSharedPreferenceChangeListener {

    // members -------------------------------------------------------------------------------------

    private var unit = ""
    private val decimalFormat =
        DecimalFormat("#,###.#").apply { roundingMode = RoundingMode.FLOOR }
    private val msToKmh = 3.6
    private val msToMph = 2.23693629

    // init ----------------------------------------------------------------------------------------

    init {
        readExistingPreference()
        registerPreferenceChangeListener()
    }

    // OnSharedPreferencesChangedListener ----------------------------------------------------------

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == context.getString(R.string.settings_preference_speed_unit_key)) {
            LogEx.d(Constants.TAG_TRANSFORMATION_SPEED, "preference changed")
            val defaultValue = context.resources.getStringArray(R.array.speed_unit_values)[0]
            unit = sharedPreferences
                .getString(
                    context.getString(R.string.settings_preference_speed_unit_key),
                    defaultValue
                )!!
        }
    }

    // public methods ------------------------------------------------------------------------------

    fun transform(speed: Float): String {
        val unitSystem = context.resources.getStringArray(R.array.speed_unit_values)

        return when (unit) {
            unitSystem[0] -> {
                "${decimalFormat.format(speed)} m/s"
            }
            unitSystem[1] -> {
                "${decimalFormat.format(speed * msToKmh)} km/h"
            }
            unitSystem[2] -> {
                "${decimalFormat.format(speed * msToMph)} mph"
            }
            else -> {
                "Unit not supported: $unit"
            }
        }
    }

    // private methods -----------------------------------------------------------------------------

    private fun readExistingPreference() {
        val defaultValue =
            context.resources.getStringArray(R.array.speed_unit_values)[0]
        unit = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(
                context.getString(R.string.settings_preference_speed_unit_key),
                defaultValue
            )!!
    }

    private fun registerPreferenceChangeListener() {
        LogEx.d(Constants.TAG_TRANSFORMATION_SPEED, "register preference change listener")
        PreferenceManager.getDefaultSharedPreferences(context)
            .registerOnSharedPreferenceChangeListener(this)
    }

    // CHECK: should it be unregistered? if so then how?
    private fun unregisterPreferenceChangeListener() {
        LogEx.d(Constants.TAG_TRANSFORMATION_SPEED, "unregister preference change listener")
        PreferenceManager.getDefaultSharedPreferences(context)
            .unregisterOnSharedPreferenceChangeListener(this)
    }
}
