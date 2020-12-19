package ns.fajnet.android.geobreadcrumbs.activities.main.live_gps

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.logger.LogEx
import ns.fajnet.android.geobreadcrumbs.common.singleArgViewModelFactory
import java.text.SimpleDateFormat

class LiveGPSFragmentViewModel(application: Application) : AndroidViewModel(application) {

    // members -------------------------------------------------------------------------------------

    private val _longitude = MutableLiveData<Double>()
    private val _latitude = MutableLiveData<Double>()
    private val _altitude = MutableLiveData<String>()
    private val _locationFixTime = MutableLiveData<String>()
    private val _accuracy = MutableLiveData<String>()
    private val _speed = MutableLiveData<String>()
    private val _bearing = MutableLiveData<String>()

    // properties ----------------------------------------------------------------------------------

    val longitude: LiveData<Double>
        get() = _longitude

    val latitude: LiveData<Double>
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

    // public methods ------------------------------------------------------------------------------

    fun setLocation(location: Location) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                LogEx.d(Constants.TAG_LIVE_GPS_FRAGMENT_VM, "location received: $location")
                val simpleDateFormat = SimpleDateFormat.getTimeInstance()

                _longitude.postValue(location.longitude)
                _latitude.postValue(location.latitude)
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
        }
    }

    // companion -----------------------------------------------------------------------------------

    companion object {
        val FACTORY = singleArgViewModelFactory(::LiveGPSFragmentViewModel)
    }
}
