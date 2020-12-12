package ns.fajnet.android.geobreadcrumbs.activities.main.current_track

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.fragment_current_track.*
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.activities.main.MainActivityViewModel
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.logger.LogEx

class CurrentTrackFragment : Fragment(), HasDefaultViewModelProviderFactory {

    // members -------------------------------------------------------------------------------------

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: CurrentTrackFragmentViewModel by viewModels()

    // overrides -----------------------------------------------------------------------------------

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_current_track, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bind()
        bindLiveData()
    }

    // HasDefaultViewModelProviderFactory ----------------------------------------------------------

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory {
        return CurrentTrackFragmentViewModel.FACTORY(requireActivity().application)
    }

    // private methods -----------------------------------------------------------------------------

    private fun bind() {
        startTrack.setOnClickListener {
            startTrackClick()
        }
        stopTrack.setOnClickListener {
            stopTrackClick()
        }
    }

    private fun bindLiveData() {
        activityViewModel.geoTrackServiceReference.observe(viewLifecycleOwner) { value ->
            LogEx.d(Constants.TAG_CURRENT_TRACK_FRAGMENT, "serviceReference updated")
            value?.recordingActive.observe(viewLifecycleOwner) { x ->
                if (x) {
                    startTrack.visibility = View.GONE
                    stopTrack.visibility = View.VISIBLE
                    currentTrackData.visibility = View.VISIBLE
                }
                else {
                    startTrack.visibility = View.VISIBLE
                    stopTrack.visibility = View.GONE
                    currentTrackData.visibility = View.GONE
                }
            }
            value?.liveUpdate.observe(viewLifecycleOwner) { dto ->
                LogEx.d(Constants.TAG_CURRENT_TRACK_FRAGMENT, "liveUpdate received")
                // TODO: use transformations for all displayed data
                durationLayout.editText?.setText(dto.duration)
                distanceLayout.editText?.setText(dto.distance.toString())
                currentSpeedLayout.editText?.setText(dto.currentSpeed.toString())
                averageSpeedLayout.editText?.setText(dto.averageSpeed.toString())
                maxSpeedLayout.editText?.setText(dto.maxSpeed.toString())
                currentBearingLayout.editText?.setText(dto.currentBearing.toString())
                overallBearingLayout.editText?.setText(dto.overallBearing.toString())
            }
        }
    }

    private fun unbindLiveData() {
        activityViewModel.geoTrackServiceReference.removeObservers(viewLifecycleOwner)
        activityViewModel.geoTrackServiceReference.value?.liveUpdate?.removeObservers(
            viewLifecycleOwner
        )
    }

    private fun startTrackClick() {
        LogEx.d(Constants.TAG_CURRENT_TRACK_FRAGMENT, "start track clicked")
        viewModel.startTrack("TODO")
//        NewPointDialog(
//            {
//                LogEx.d(Constants.TAG_CURRENT_TRACK_FRAGMENT, "dialog OK: $it")
//                viewModel.startTrack(it)
//            },
//            {
//                LogEx.w(Constants.TAG_CURRENT_TRACK_FRAGMENT, "dialog Cancel")
//            }
//        ).show(childFragmentManager, "newPoint")

        unbindLiveData()
        bindLiveData()
    }

    private fun stopTrackClick() {
        LogEx.d(Constants.TAG_CURRENT_TRACK_FRAGMENT, "stop track clicked")
        unbindLiveData()
        viewModel.stopTrack(activityViewModel.geoTrackServiceReference)
    }

    // companion -----------------------------------------------------------------------------------

    companion object {
        fun newInstance() = CurrentTrackFragment()
    }
}
