package ns.fajnet.android.geobreadcrumbs.activities.main.recorded_tracks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.fragment_recorded_tracks.*
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.LogEx

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
        viewModel.loadTracks()
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
        viewModel.recordedTracksAdapter.observe(viewLifecycleOwner) {
            listView.adapter = it
            LogEx.d(Constants.TAG_RECORDED_TRACKS_FRAGMENT, "recycler view set up")
        }
    }

    // companion -----------------------------------------------------------------------------------

    companion object {

        @JvmStatic
        fun newInstance() = RecordedTracksFragment()
    }
}
