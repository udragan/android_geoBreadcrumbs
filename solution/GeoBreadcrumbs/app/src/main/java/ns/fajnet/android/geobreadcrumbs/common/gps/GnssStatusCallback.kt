package ns.fajnet.android.geobreadcrumbs.common.gps

import android.location.GnssStatus
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.LogEx

@RequiresApi(Build.VERSION_CODES.N)
class GnssStatusCallback : GnssStatus.Callback() {

    // members -------------------------------------------------------------------------------------

    private val _noOfSatellites = MutableLiveData<Int>()

    // overrides -----------------------------------------------------------------------------------

    override fun onSatelliteStatusChanged(status: GnssStatus) {
        super.onSatelliteStatusChanged(status)
        LogEx.d(Constants.TAG_GNSS_STATUS_CALLBACK, "$status")
        val satellites = status.satelliteCount
        var satellitesInFix = 0

        for (satelliteIndex in 0 until satellites - 1) {
            if (status.usedInFix(satelliteIndex)) {
                satellitesInFix++
            }
        }

        LogEx.d(
            Constants.TAG_GNSS_STATUS_CALLBACK, "Used In Last Fix ($satellitesInFix) / $satellites"
        )

        _noOfSatellites.postValue(satellitesInFix)
    }

    // properties ----------------------------------------------------------------------------------

    val noOfSatellites: LiveData<Int>
        get() = _noOfSatellites
}
