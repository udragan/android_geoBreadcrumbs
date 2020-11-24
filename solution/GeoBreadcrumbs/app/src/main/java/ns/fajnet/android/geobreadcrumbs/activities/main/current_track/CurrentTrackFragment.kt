package ns.fajnet.android.geobreadcrumbs.activities.main.current_track

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModelProvider
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.dialogs.NewPointDialog
import ns.fajnet.android.geobreadcrumbs.common.logger.LogEx

class CurrentTrackFragment : Fragment(), HasDefaultViewModelProviderFactory {

    // members -------------------------------------------------------------------------------------

    private val viewModel: CurrentTrackFragmentViewModel by viewModels()

    private lateinit var fragmentView: View
    private lateinit var startTrack: AppCompatButton
    private lateinit var stopTrack: AppCompatButton

    // overrides -----------------------------------------------------------------------------------

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_current_track, container, false)
        findViews()
        bind()
        bindLiveData()

        return fragmentView
    }

    // HasDefaultViewModelProviderFactory ----------------------------------------------------------

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory {
        return CurrentTrackFragmentViewModel.FACTORY(requireActivity().application)
    }

    // private methods -----------------------------------------------------------------------------

    private fun findViews() {
        startTrack = fragmentView.findViewById(R.id.start_track)
        stopTrack = fragmentView.findViewById(R.id.stop_track)
    }

    private fun bind() {
        startTrack.setOnClickListener {
            startTrackClick()
        }
        stopTrack.setOnClickListener {
            stopTrackClick()
        }
    }

    private fun bindLiveData() {
        //TODO("Not yet implemented")
    }

    private fun startTrackClick() {
        LogEx.d(Constants.TAG_CURRENT_TRACK_FRAGMENT, "start track clicked")
        NewPointDialog(
            {
                LogEx.d(Constants.TAG_CURRENT_TRACK_FRAGMENT, "dialog OK: $it")
                viewModel.startTrack(it)
            },
            {
                LogEx.w(Constants.TAG_CURRENT_TRACK_FRAGMENT, "dialog Cancel")
            }
        ).show(childFragmentManager, "newPoint")
    }

    private fun stopTrackClick() {
        LogEx.d(Constants.TAG_CURRENT_TRACK_FRAGMENT, "stop track clicked")
        viewModel.stopTrack()
    }

    // companion -----------------------------------------------------------------------------------

    companion object {
        fun newInstance() = CurrentTrackFragment()
    }
}
