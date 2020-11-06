package ns.fajnet.android.geobreadcrumbs.activities.main.current_track

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import ns.fajnet.android.geobreadcrumbs.AppInit
import ns.fajnet.android.geobreadcrumbs.common.singleArgViewModelFactory
import ns.fajnet.android.geobreadcrumbs.services.GeoTrackService

class CurrentTrackFragmentViewModel(application: Application) : AndroidViewModel(application) {

    // members -------------------------------------------------------------------------------------

    private val context: Context = getApplication<AppInit>().applicationContext

    // public methods ------------------------------------------------------------------------------

    fun startTrack(startPointName: String) {
        val intent = Intent(context, GeoTrackService::class.java)
        intent.putExtra(GeoTrackService.EXTRA_START_POINT_NAME, startPointName)
        ContextCompat.startForegroundService(context, intent)
    }

    fun stopTrack() {
        val intent = Intent(context, GeoTrackService::class.java)
        context.stopService(intent)
    }

    // companion -----------------------------------------------------------------------------------

    companion object {
        val FACTORY = singleArgViewModelFactory(::CurrentTrackFragmentViewModel)
    }
}
