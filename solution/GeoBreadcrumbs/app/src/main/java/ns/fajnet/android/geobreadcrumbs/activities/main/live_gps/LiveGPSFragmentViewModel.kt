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
import ns.fajnet.android.geobreadcrumbs.common.LogEx
import ns.fajnet.android.geobreadcrumbs.common.gps.GpsStatus
import ns.fajnet.android.geobreadcrumbs.common.singleArgViewModelFactory
import java.text.SimpleDateFormat

class LiveGPSFragmentViewModel(application: Application) : AndroidViewModel(application) {

    // members -------------------------------------------------------------------------------------

    private val _longitude = MutableLiveData<Double>()
    private val _latitude = MutableLiveData<Double>()
    private val _altitude = MutableLiveData<Double>()
    private val _locationFixTime = MutableLiveData<String>()
    private val _accuracy = MutableLiveData<Float>()
    private val _speed = MutableLiveData<Float>()
    private val _bearing = MutableLiveData<Float>()

    private val _gpsStatus = GpsStatus(getApplication())

    // overrides -----------------------------------------------------------------------------------

    override fun onCleared() {
        super.onCleared()
        _gpsStatus.dispose()
    }

    // properties ----------------------------------------------------------------------------------

    val longitude: LiveData<Double>
        get() = _longitude

    val latitude: LiveData<Double>
        get() = _latitude

    val altitude: LiveData<Double>
        get() = _altitude

    val locationFixTime: LiveData<String>
        get() = _locationFixTime

    val accuracy: LiveData<Float>
        get() = _accuracy

    val speed: LiveData<Float>
        get() = _speed

    val bearing: LiveData<Float>
        get() = _bearing

    val noOfSatellites: LiveData<Int>
        get() = _gpsStatus.noOfSatellites

    // public methods ------------------------------------------------------------------------------

    fun requestGpsStatus() {
        _gpsStatus.initialize()
    }

    fun setLocation(location: Location) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                LogEx.d(Constants.TAG_LIVE_GPS_FRAGMENT_VM, "location received: $location")
                val simpleDateFormat = SimpleDateFormat.getTimeInstance()

                _longitude.postValue(location.longitude)
                _latitude.postValue(location.latitude)
                _altitude.postValue(location.altitude)
                _locationFixTime.postValue(
                    simpleDateFormat.format(
                        location.time
                    )
                )
                _accuracy.postValue(location.accuracy)
                _speed.postValue(location.speed)
                _bearing.postValue(location.bearing)
            }
        }
    }

    // companion -----------------------------------------------------------------------------------

    companion object {
        val FACTORY = singleArgViewModelFactory(::LiveGPSFragmentViewModel)
    }
}
