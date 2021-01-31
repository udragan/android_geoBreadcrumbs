package ns.fajnet.android.geobreadcrumbs.activities.main.recorded_tracks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ns.fajnet.android.geobreadcrumbs.common.singleArgViewModelFactory
import ns.fajnet.android.geobreadcrumbs.database.Track
import ns.fajnet.android.geobreadcrumbs.repositories.ServiceRepository

class RecordedTracksFragmentViewModel(application: Application) : AndroidViewModel(application) {

    // properties ----------------------------------------------------------------------------------

    val adapter = RecordedTracksAdapter(application)

    // public methods ------------------------------------------------------------------------------

    fun tracksUpdated(tracks: List<Track>) {
        adapter.dataSet = tracks.toTypedArray()
    }

    fun renameTrack(track: Track, name: String) {
        ServiceRepository.geoTrackServiceReference.value?.renameTrack(track, name)
    }

    fun releaseAdapter() {
        adapter.detach()
    }

    // companion -----------------------------------------------------------------------------------

    companion object {
        val FACTORY = singleArgViewModelFactory(::RecordedTracksFragmentViewModel)
    }
}
