package ns.fajnet.android.geobreadcrumbs.activities.main.recorded_tracks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_recorded_tracks.*
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.logger.LogEx

class RecordedTracksFragment : Fragment(), HasDefaultViewModelProviderFactory {

    // members -------------------------------------------------------------------------------------

    private lateinit var viewModel: RecordedTracksFragmentViewModel

    // overrides -----------------------------------------------------------------------------------

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recorded_tracks, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RecordedTracksFragmentViewModel::class.java)
        recycler_view.layoutManager = LinearLayoutManager(requireContext())

        // TODO: Use the ViewModel
        bindLiveData()
    }

    override fun onResume() {
        LogEx.d(Constants.TAG_RECORDED_TRACKS_FRAGMENT, "onResume")
        super.onResume()
        viewModel.loadTracks()
    }

    // HasDefaultViewModelProviderFactory ----------------------------------------------------------

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory {
        return RecordedTracksFragmentViewModel.FACTORY(requireActivity().application)
    }

    // private methods -----------------------------------------------------------------------------

    private fun bindLiveData() {
        viewModel.recordedTracksAdapter.observe(viewLifecycleOwner) {
            recycler_view.adapter = it
            LogEx.d(Constants.TAG_RECORDED_TRACKS_FRAGMENT, "recycler view set up")
        }
    }

    // companion -----------------------------------------------------------------------------------

    companion object {

        @JvmStatic
        fun newInstance() = RecordedTracksFragment()
    }
}
