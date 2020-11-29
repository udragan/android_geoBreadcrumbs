package ns.fajnet.android.geobreadcrumbs.activities.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.logger.LogEx
import ns.fajnet.android.geobreadcrumbs.services.GeoTrackService

class MainActivityViewModel : ViewModel() {

    // members -------------------------------------------------------------------------------------

    private var _geoTrackServiceReference = MutableLiveData<GeoTrackService>()

    // properties ----------------------------------------------------------------------------------

    val geoTrackServiceReference: LiveData<GeoTrackService>
        get() = _geoTrackServiceReference

    // public methods ------------------------------------------------------------------------------

    fun setGeoTrackServiceReference(geoTrackServiceReference: GeoTrackService) {
        LogEx.d(Constants.TAG_MAIN_ACTIVITY_VM, "setting GeoTrackServiceReference")
        _geoTrackServiceReference.postValue(geoTrackServiceReference)
    }

    fun unsetGeoTrackServiceReference() {
        LogEx.d(Constants.TAG_MAIN_ACTIVITY_VM, "unsetting GeoTrackServiceReference")
        _geoTrackServiceReference.postValue(null)
    }
}
