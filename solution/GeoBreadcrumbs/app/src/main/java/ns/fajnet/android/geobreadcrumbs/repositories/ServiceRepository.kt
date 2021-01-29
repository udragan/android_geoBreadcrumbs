package ns.fajnet.android.geobreadcrumbs.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.LogEx
import ns.fajnet.android.geobreadcrumbs.services.GeoTrackService

object ServiceRepository {

    // members -------------------------------------------------------------------------------------

    private var geoTrackServiceReferenceMutable = MutableLiveData<GeoTrackService>()

    // properties ----------------------------------------------------------------------------------

    val geoTrackServiceReference: LiveData<GeoTrackService>
        get() = geoTrackServiceReferenceMutable

    // public methods ------------------------------------------------------------------------------

    fun setGeoTrackServiceReference(geoTrackServiceReference: GeoTrackService) {
        LogEx.d(Constants.TAG_SERVICE_REPOSITORY, "setting GeoTrackServiceReference")
        geoTrackServiceReferenceMutable.postValue(geoTrackServiceReference)
    }

    fun unsetGeoTrackServiceReference() {
        LogEx.d(Constants.TAG_SERVICE_REPOSITORY, "unsetting GeoTrackServiceReference")
        geoTrackServiceReferenceMutable.postValue(null)
    }
}
