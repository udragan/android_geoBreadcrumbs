package ns.fajnet.android.geobreadcrumbs.activities.main.recorded_tracks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_recorded_tracks.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.LogEx
import ns.fajnet.android.geobreadcrumbs.common.MultiChoiceModeListener
import ns.fajnet.android.geobreadcrumbs.common.dialogs.RenameTrackDialog
import ns.fajnet.android.geobreadcrumbs.database.GeoBreadcrumbsDatabase
import ns.fajnet.android.geobreadcrumbs.database.Track

class RecordedTracksFragment : Fragment(), HasDefaultViewModelProviderFactory {

    // members -------------------------------------------------------------------------------------

    private val viewModel: RecordedTracksFragmentViewModel by viewModels()

    // overrides -----------------------------------------------------------------------------------

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recorded_tracks, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindLiveData()

        with(listView) {
            choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
            val listener = MultiChoiceModeListener(this)

            listener.renameTrackDelegate = { renameTrackHandler(it) }

            setMultiChoiceModeListener(listener)
            adapter = viewModel.adapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releaseAdapter()
    }

    // HasDefaultViewModelProviderFactory ----------------------------------------------------------

    override fun getDefaultViewModelProviderFactory() =
        RecordedTracksFragmentViewModel.FACTORY(requireActivity().application)

    // private methods -----------------------------------------------------------------------------

    private fun bindLiveData() {
        GeoBreadcrumbsDatabase.getInstance(requireActivity())
            .trackDao
            .getAllLive()
            .observe(viewLifecycleOwner,
                Observer<List<Track>> {
                    CoroutineScope(Dispatchers.Main).launch {
                        LogEx.e(Constants.TAG_RECORDED_TRACKS_FRAGMENT, "observer triggered")
                        viewModel.tracksUpdated(it)
                    }
                }
            )
    }

    private fun renameTrackHandler(track: Track) {
        RenameTrackDialog(track) {
            viewModel.renameTrack(track, it)
        }.show(childFragmentManager, "")
    }

    // companion -----------------------------------------------------------------------------------

    companion object {

        @JvmStatic
        fun newInstance() = RecordedTracksFragment()
    }
}
