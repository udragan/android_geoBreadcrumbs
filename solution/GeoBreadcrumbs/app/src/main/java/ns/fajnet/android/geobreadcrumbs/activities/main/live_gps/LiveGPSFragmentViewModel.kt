package ns.fajnet.android.geobreadcrumbs.activities.main.live_gps

import android.app.Application
import android.content.SharedPreferences
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.common.*
import java.text.SimpleDateFormat

class LiveGPSFragmentViewModel(application: Application) :
    AndroidViewModel(application), SharedPreferences.OnSharedPreferenceChangeListener {

    init {
        readExistingPreferences()
        registerPreferenceChangeListener()
    }

    // members -------------------------------------------------------------------------------------

    private lateinit var coordinateDisplayTransformation: CoordinateDisplayTransformation

    private val _longitude = MutableLiveData<String>()
    private val _latitude = MutableLiveData<String>()
    private val _altitude = MutableLiveData<String>()
    private val _locationFixTime = MutableLiveData<String>()
    private val _accuracy = MutableLiveData<String>()
    private val _speed = MutableLiveData<String>()
    private val _bearing = MutableLiveData<String>()

    // overrides -----------------------------------------------------------------------------------

    override fun onCleared() {
        super.onCleared()
        unregisterPreferenceChangeListener()
    }

    // properties ----------------------------------------------------------------------------------

    val longitude: LiveData<String>
        get() = _longitude

    val latitude: LiveData<String>
        get() = _latitude

    val altitude: LiveData<String>
        get() = _altitude

    val locationFixTime: LiveData<String>
        get() = _locationFixTime

    val accuracy: LiveData<String>
        get() = _accuracy

    val speed: LiveData<String>
        get() = _speed

    val bearing: LiveData<String>
        get() = _bearing

    // OnSharedPreferencesChangedListener ----------------------------------------------------------

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        Log.d(Constants.TAG_LIVE_GPS_FRAGMENT_VM, "preference changed")

        if (key == getApplication<AppInit>().getString(R.string.settings_preference_coordinates_display_key)) {
            val defaultValue =
                getApplication<AppInit>().resources.getStringArray(R.array.coordinates_display_values)[0]
            val preferenceValue = sharedPreferences
                .getString(
                    getApplication<AppInit>().getString(R.string.settings_preference_coordinates_display_key),
                    defaultValue
                )
            coordinateDisplayTransformation.transformToDegMinSec = preferenceValue != defaultValue
        }
    }
    // public methods ------------------------------------------------------------------------------

    fun setLocation(location: Location) {
        Log.d(Constants.TAG_LIVE_GPS_FRAGMENT_VM, "location received: $location")
        val simpleDateFormat = SimpleDateFormat.getTimeInstance()

        _longitude.postValue(
            coordinateDisplayTransformation.transform(
                location.longitude,
                Orientation.HORIZONTAL
            )
        )
        _latitude.postValue(
            coordinateDisplayTransformation.transform(
                location.latitude,
                Orientation.VERTICAL
            )
        )
        _altitude.postValue(location.altitude.toString())
        _locationFixTime.postValue(
            simpleDateFormat.format(
                location.time
            )
        )
        _accuracy.postValue(location.accuracy.toString())
        _speed.postValue(location.speed.toString())
        _bearing.postValue(location.bearing.toString())
    }

    // private methods -----------------------------------------------------------------------------

    private fun readExistingPreferences() {
        val defaultCoordinateDisplayPreference =
            getApplication<AppInit>().resources.getStringArray(R.array.coordinates_display_values)[0]
        val coordinateDisplayPreference =
            PreferenceManager.getDefaultSharedPreferences(getApplication<AppInit>().applicationContext)
                .getString(
                    getApplication<AppInit>().getString(R.string.settings_preference_coordinates_display_key),
                    defaultCoordinateDisplayPreference
                )
        coordinateDisplayTransformation =
            CoordinateDisplayTransformation(coordinateDisplayPreference != defaultCoordinateDisplayPreference)
    }

    private fun registerPreferenceChangeListener() {
        Log.d(Constants.TAG_LIVE_GPS_FRAGMENT_VM, "register preference change listener")
        PreferenceManager.getDefaultSharedPreferences(getApplication<AppInit>().applicationContext)
            .registerOnSharedPreferenceChangeListener(this)
    }

    private fun unregisterPreferenceChangeListener() {
        Log.d(Constants.TAG_LIVE_GPS_FRAGMENT_VM, "unregister preference change listener")
        PreferenceManager.getDefaultSharedPreferences(getApplication<AppInit>().applicationContext)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    // companion -----------------------------------------------------------------------------------

    companion object {
        val FACTORY = singleArgViewModelFactory(::LiveGPSFragmentViewModel)
    }
}
