package ns.fajnet.android.geobreadcrumbs.activities.main.recorded_tracks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ns.fajnet.android.geobreadcrumbs.AppInit
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.logger.LogEx
import ns.fajnet.android.geobreadcrumbs.common.singleArgViewModelFactory
import ns.fajnet.android.geobreadcrumbs.database.GeoBreadcrumbsDatabase

class RecordedTracksFragmentViewModel(application: Application) : AndroidViewModel(application) {

    // members -------------------------------------------------------------------------------------

    private var isDataLoaded = false
    private var _recordedTracksAdapter = MutableLiveData<RecordedTracksAdapter>()

    // properties ----------------------------------------------------------------------------------

    val recordedTracksAdapter: LiveData<RecordedTracksAdapter>
        get() = _recordedTracksAdapter

    // public methods ------------------------------------------------------------------------------

    fun loadTracks() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (!isDataLoaded) {
                    LogEx.d(Constants.TAG_RECORDED_TRACKS_FRAGMENT_VM, "load tracks")
                    isDataLoaded = true

                    val tracks =
                        GeoBreadcrumbsDatabase.getInstance(super.getApplication<AppInit>().applicationContext)
                            .trackDao
                            .getAll()

                    // TODO: remove
                    //Thread.sleep(5000)

                    LogEx.d(Constants.TAG_RECORDED_TRACKS_FRAGMENT_VM, "loading finished")
                    _recordedTracksAdapter.postValue(RecordedTracksAdapter(getApplication<AppInit>().applicationContext, tracks))
                    LogEx.d(Constants.TAG_RECORDED_TRACKS_FRAGMENT_VM, "adapter set")
                }
            }
        }
    }

    // companion -----------------------------------------------------------------------------------

    companion object {
        val FACTORY = singleArgViewModelFactory(::RecordedTracksFragmentViewModel)
    }
}
