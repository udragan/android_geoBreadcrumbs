@file:Suppress("DEPRECATION")

package ns.fajnet.android.geobreadcrumbs.common.gps

import android.annotation.SuppressLint
import android.content.Context
import android.location.GpsStatus
import android.location.LocationManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.LogEx

class GpsStatusListener(val context: Context) : GpsStatus.Listener {

    // members -------------------------------------------------------------------------------------

    private val _noOfSatellites = MutableLiveData<Int>()

    // overrides -----------------------------------------------------------------------------------

    @SuppressLint("MissingPermission")
    override fun onGpsStatusChanged(event: Int) {
        when (event) {
            GpsStatus.GPS_EVENT_STARTED -> LogEx.d(
                Constants.TAG_GPS_STATUS_LISTENER,
                "changed started"
            )
            GpsStatus.GPS_EVENT_STOPPED -> LogEx.d(
                Constants.TAG_GPS_STATUS_LISTENER,
                "changed stopped"
            )
            GpsStatus.GPS_EVENT_FIRST_FIX -> LogEx.d(
                Constants.TAG_GPS_STATUS_LISTENER,
                "changed first fix"
            )
            GpsStatus.GPS_EVENT_SATELLITE_STATUS -> {
                LogEx.d(Constants.TAG_GPS_STATUS_LISTENER, "changed status")
                val locationManager =
                    context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                var satellites = 0
                var satellitesInFix = 0

                val allSatellites = locationManager.getGpsStatus(null)?.satellites

                if (allSatellites != null) {
                    for (sat in allSatellites) {
                        if (sat.usedInFix()) {
                            satellitesInFix++
                        }
                        satellites++
                    }
                }
                LogEx.e(
                    Constants.TAG_GPS_STATUS_LISTENER,
                    "Used In Last Fix ($satellitesInFix) / $satellites"
                )

                _noOfSatellites.postValue(satellitesInFix)
            }
        }
    }

    // properties ----------------------------------------------------------------------------------

    val noOfSatellites: LiveData<Int>
        get() = _noOfSatellites

}
