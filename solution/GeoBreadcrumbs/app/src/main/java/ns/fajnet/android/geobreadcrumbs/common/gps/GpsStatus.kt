package ns.fajnet.android.geobreadcrumbs.common.gps

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.LogEx
import kotlin.random.Random

class GpsStatus(private val app: Application) {

    // members -------------------------------------------------------------------------------------

    private lateinit var _gnssStatusCallback: GnssStatusCallback
    private lateinit var _noOfSatellitesObserver: Observer<Int>

    private val _noOfSatellites = MutableLiveData<Int>()

    // properties ----------------------------------------------------------------------------------

    val noOfSatellites: LiveData<Int>
        get() = _noOfSatellites

    // public methods ------------------------------------------------------------------------------

    fun initialize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // use new GnssStatus
            LogEx.i(
                Constants.TAG_GPS_STATUS,
                "OS build version is: ${Build.VERSION.SDK_INT}, using GnssStatus as gps status provider"
            )

            if (ActivityCompat.checkSelfPermission(
                    app,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val locationManager =
                    app.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                _noOfSatellitesObserver = Observer { value ->
                    // TODO: return random 1..5 for testing
                    _noOfSatellites.postValue(Random.nextInt(1, 6))
                    //_noOfSatellites.postValue(value)
                }
                _gnssStatusCallback = GnssStatusCallback().apply {
                    noOfSatellites.observeForever(_noOfSatellitesObserver)
                }
                // TODO: use non deprecated method!
                locationManager.registerGnssStatusCallback(_gnssStatusCallback)
                LogEx.i(
                    Constants.TAG_GPS_STATUS,
                    "GnssStatusCallback initialized and registered"
                )
            }
        } else {
            // use old GpsStatus
        }
    }

    fun dispose() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (this::_gnssStatusCallback.isInitialized) {
                val locationManager =
                    app.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                _gnssStatusCallback.noOfSatellites.removeObserver(_noOfSatellitesObserver)
                locationManager.unregisterGnssStatusCallback(_gnssStatusCallback)
                LogEx.i(Constants.TAG_GPS_STATUS, "Unregistering GnssStatusCallback")
            }
        } else {

        }
    }
}