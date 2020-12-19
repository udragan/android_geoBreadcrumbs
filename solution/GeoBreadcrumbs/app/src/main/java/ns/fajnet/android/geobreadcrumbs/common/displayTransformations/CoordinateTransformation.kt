package ns.fajnet.android.geobreadcrumbs.common.displayTransformations

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import androidx.preference.PreferenceManager
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.logger.LogEx
import kotlin.math.abs

class CoordinateTransformation(val context: Context) :
    SharedPreferences.OnSharedPreferenceChangeListener {

    // members -------------------------------------------------------------------------------------

    private var unit = ""

    // init ----------------------------------------------------------------------------------------

    init {
        readExistingPreference()
        registerPreferenceChangeListener()
    }

    // OnSharedPreferencesChangedListener ----------------------------------------------------------

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        LogEx.d(Constants.TAG_TRANSFORMATION_COORDINATE, "preference changed")
        val defaultValue = context.resources.getStringArray(R.array.unit_coordinate_values)[0]
        unit = sharedPreferences
            .getString(
                context.getString(R.string.settings_preference_unit_coordinate_key),
                defaultValue
            )!!
    }

    // public methods ------------------------------------------------------------------------------

    fun transform(coordinate: Double, orientation: Orientation): String {
        val unitSystem = context.resources.getStringArray(R.array.unit_coordinate_values)

        return when (unit) {
            unitSystem[0] -> {
                "${coordinate}º"
            }
            unitSystem[1] -> {
                val direction = getDirection(coordinate, orientation)
                val splitCoordinates =
                    arrayOf(Location.convert(abs(coordinate), Location.FORMAT_SECONDS).split(':'))

                "${splitCoordinates[0][0]}º ${splitCoordinates[0][1]}\" ${splitCoordinates[0][2]}' $direction"
            }
            else -> {
                "Unit not supported: $unit"
            }
        }
    }

    // private methods -----------------------------------------------------------------------------

    private fun readExistingPreference() {
        val defaultValue =
            context.resources.getStringArray(R.array.unit_speed_values)[0]
        unit = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(
                context.getString(R.string.settings_preference_unit_coordinate_key),
                defaultValue
            )!!
    }

    private fun registerPreferenceChangeListener() {
        LogEx.d(Constants.TAG_TRANSFORMATION_COORDINATE, "register preference change listener")
        PreferenceManager.getDefaultSharedPreferences(context)
            .registerOnSharedPreferenceChangeListener(this)
    }

    // CHECK: should it be unregistered? if so then how?
    private fun unregisterPreferenceChangeListener() {
        LogEx.d(Constants.TAG_TRANSFORMATION_COORDINATE, "unregister preference change listener")
        PreferenceManager.getDefaultSharedPreferences(context)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun getDirection(coordinate: Double, orientation: Orientation): String {
        return if (orientation == Orientation.HORIZONTAL) {
            if (coordinate < 0) {
                Direction.W.name
            } else {
                Direction.E.name
            }
        } else if (coordinate < 0) {
            Direction.S.name
        } else {
            Direction.N.name
        }
    }
}
