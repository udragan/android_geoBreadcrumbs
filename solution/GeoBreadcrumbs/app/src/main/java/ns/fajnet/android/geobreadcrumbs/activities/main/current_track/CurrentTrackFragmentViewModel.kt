package ns.fajnet.android.geobreadcrumbs.activities.main.current_track

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import ns.fajnet.android.geobreadcrumbs.AppInit
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.LogEx
import ns.fajnet.android.geobreadcrumbs.common.singleArgViewModelFactory
import ns.fajnet.android.geobreadcrumbs.services.GeoTrackService

class CurrentTrackFragmentViewModel(application: Application) : AndroidViewModel(application) {

    // members -------------------------------------------------------------------------------------

    private val context: Context = getApplication<AppInit>().applicationContext

    // public methods ------------------------------------------------------------------------------

    fun startTrack(startPointName: String) {
        LogEx.i(Constants.TAG_CURRENT_TRACK_FRAGMENT_VM, "start track")
        val intent = Intent(context, GeoTrackService::class.java)
        intent.putExtra(GeoTrackService.EXTRA_START_POINT_NAME, startPointName)
        ContextCompat.startForegroundService(context, intent)
    }

    fun stopTrack(service: LiveData<GeoTrackService>) {
        LogEx.i(Constants.TAG_CURRENT_TRACK_FRAGMENT_VM, "stop track")
        service.value?.stopRecordingTrack()
    }

    // companion -----------------------------------------------------------------------------------

    companion object {
        val FACTORY = singleArgViewModelFactory(::CurrentTrackFragmentViewModel)
    }
}
