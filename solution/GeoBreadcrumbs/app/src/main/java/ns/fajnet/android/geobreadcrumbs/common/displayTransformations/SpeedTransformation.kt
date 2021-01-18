package ns.fajnet.android.geobreadcrumbs.common.displayTransformations

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.LogEx
import java.math.RoundingMode
import java.text.DecimalFormat

class SpeedTransformation(val context: Context) : IDisplayTransformation,
    SharedPreferences.OnSharedPreferenceChangeListener {

    // members -------------------------------------------------------------------------------------

    private var unit = ""
    private val decimalFormat =
        DecimalFormat("#,###.#").apply { roundingMode = RoundingMode.HALF_EVEN }
    private val msToKmh = 3.6
    private val msToMph = 2.23693629

    private val subscribers = mutableListOf<() -> Unit>()

    // init ----------------------------------------------------------------------------------------

    init {
        readExistingPreference()
        registerPreferenceChangeListener()
    }

    // overrides -----------------------------------------------------------------------------------

    override fun dispose() {
        unregisterPreferenceChangeListener()
        unsubscribeAll()
    }

    // OnSharedPreferencesChangedListener ----------------------------------------------------------

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == context.getString(R.string.settings_preference_unit_speed_key)) {
            LogEx.d(Constants.TAG_TRANSFORMATION_SPEED, "preference changed")
            val defaultValue = context.resources.getStringArray(R.array.unit_speed_values)[0]
            unit = sharedPreferences
                .getString(
                    context.getString(R.string.settings_preference_unit_speed_key),
                    defaultValue
                )!!

            LogEx.d(
                Constants.TAG_TRANSFORMATION_SPEED,
                "triggering ${subscribers.size} subscribers"
            )
            subscribers.forEach { x -> x.invoke() }
        }
    }

    // public methods ------------------------------------------------------------------------------

    fun transform(speed: Float): String {
        val unitSystem = context.resources.getStringArray(R.array.unit_speed_values)

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

    fun subscribe(action: () -> Unit) {
        subscribers.add(action)
    }

    fun unsubscribe(action: () -> Unit) {
        subscribers.remove { action }
    }

    // private methods -----------------------------------------------------------------------------

    private fun readExistingPreference() {
        val defaultValue =
            context.resources.getStringArray(R.array.unit_speed_values)[0]
        unit = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(
                context.getString(R.string.settings_preference_unit_speed_key),
                defaultValue
            )!!
        LogEx.d(Constants.TAG_TRANSFORMATION_SPEED, "read current unit: $unit")
    }

    private fun registerPreferenceChangeListener() {
        LogEx.d(Constants.TAG_TRANSFORMATION_SPEED, "register preference change listener")
        PreferenceManager.getDefaultSharedPreferences(context)
            .registerOnSharedPreferenceChangeListener(this)
    }

    private fun unregisterPreferenceChangeListener() {
        LogEx.d(Constants.TAG_TRANSFORMATION_SPEED, "unregister preference change listener")
        PreferenceManager.getDefaultSharedPreferences(context)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun unsubscribeAll() {
        subscribers.clear()
    }
}
