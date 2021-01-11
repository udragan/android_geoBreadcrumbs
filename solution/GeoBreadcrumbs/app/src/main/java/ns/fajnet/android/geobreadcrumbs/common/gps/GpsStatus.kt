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
    private lateinit var _gpsStatusListener: GpsStatusListener
    private lateinit var _noOfSatellitesObserver: Observer<Int>

    private val _noOfSatellites = MutableLiveData<Int>()

    // properties ----------------------------------------------------------------------------------

    val noOfSatellites: LiveData<Int>
        get() = _noOfSatellites

    // public methods ------------------------------------------------------------------------------

    fun initialize() {
        LogEx.i(Constants.TAG_GPS_STATUS, "OS build version is: ${Build.VERSION.SDK_INT}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LogEx.i(Constants.TAG_GPS_STATUS, "Using GnssStatus as gps status provider")

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
            LogEx.i(Constants.TAG_GPS_STATUS, "Using GpsStatus as gps status provider")
            val locationManager =
                app.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            _noOfSatellitesObserver = Observer { value ->
                _noOfSatellites.postValue(value)
            }
            _gpsStatusListener = GpsStatusListener(app).apply {
                noOfSatellites.observeForever(_noOfSatellitesObserver)
            }

            @Suppress("DEPRECATION", "Intentional for appropriate android version.")
            locationManager.addGpsStatusListener(_gpsStatusListener)
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
            if (this::_gpsStatusListener.isInitialized) {
                val locationManager =
                    app.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                _gpsStatusListener.noOfSatellites.removeObserver(_noOfSatellitesObserver)
                @Suppress("DEPRECATION", "Intentional for appropriate android version.")
                locationManager.removeGpsStatusListener(_gpsStatusListener)
                LogEx.i(Constants.TAG_GPS_STATUS, "Unregistering GpsStatusListener")
            }
        }
    }
}
