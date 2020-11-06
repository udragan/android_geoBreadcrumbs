package ns.fajnet.android.geobreadcrumbs.activities.main.current_track

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.dialogs.NewPointDialog

class CurrentTrackFragment : Fragment() {

    // members -------------------------------------------------------------------------------------

    private lateinit var viewModel: CurrentTrackFragmentViewModel

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

        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CurrentTrackFragmentViewModel::class.java)
        bindLiveData()
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
        Log.d(Constants.TAG_CURRENT_TRACK_FRAGMENT, "start track clicked")
        NewPointDialog(
            {
                Log.d(Constants.TAG_CURRENT_TRACK_FRAGMENT, "dialog OK: $it")
                viewModel.startTrack(it)
            },
            {
                Log.w(Constants.TAG_CURRENT_TRACK_FRAGMENT, "dialog Cancel")
            }
        ).show(childFragmentManager, "newPoint")
    }

    private fun stopTrackClick() {
        Log.d(Constants.TAG_CURRENT_TRACK_FRAGMENT, "stop track clicked")
        viewModel.stopTrack()
    }

    // companion -----------------------------------------------------------------------------------

    companion object {
        fun newInstance() = CurrentTrackFragment()
    }
}
