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
import ns.fajnet.android.geobreadcrumbs.common.LogEx
import ns.fajnet.android.geobreadcrumbs.common.displayTransformations.DistanceTransformation
import ns.fajnet.android.geobreadcrumbs.common.displayTransformations.DurationTransformation
import ns.fajnet.android.geobreadcrumbs.common.displayTransformations.HeadingTransformation
import ns.fajnet.android.geobreadcrumbs.common.displayTransformations.SpeedTransformation

class CurrentTrackFragment : Fragment(), HasDefaultViewModelProviderFactory {

    // members -------------------------------------------------------------------------------------

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: CurrentTrackFragmentViewModel by viewModels()
    private lateinit var durationTransformation: DurationTransformation
    private lateinit var distanceTransformation: DistanceTransformation
    private lateinit var speedTransformation: SpeedTransformation
    private lateinit var headingTransformation: HeadingTransformation

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

        durationTransformation = DurationTransformation()
        distanceTransformation = DistanceTransformation(requireContext())
        speedTransformation = SpeedTransformation(requireContext())
        headingTransformation = HeadingTransformation()
    }

    private fun bindLiveData() {
        activityViewModel.geoTrackServiceReference.observe(viewLifecycleOwner) { value ->
            LogEx.d(Constants.TAG_CURRENT_TRACK_FRAGMENT, "serviceReference updated")
            value?.recordingActive.observe(viewLifecycleOwner) {
                if (it) {
                    startTrack.visibility = View.GONE
                    stopTrack.visibility = View.VISIBLE
                    currentTrackData.visibility = View.VISIBLE
                } else {
                    startTrack.visibility = View.VISIBLE
                    stopTrack.visibility = View.GONE
                    currentTrackData.visibility = View.GONE
                }
            }
            value?.liveUpdate.observe(viewLifecycleOwner) {
                LogEx.d(Constants.TAG_CURRENT_TRACK_FRAGMENT, "liveUpdate received")
                durationLayout.editText?.setText(durationTransformation.transform(it.duration))
                distanceLayout.editText?.setText(distanceTransformation.transform(it.distance))
                currentSpeedLayout.editText?.setText(speedTransformation.transform(it.currentSpeed))
                averageSpeedLayout.editText?.setText(speedTransformation.transform(it.averageSpeed))
                maxSpeedLayout.editText?.setText(speedTransformation.transform(it.maxSpeed))
                currentBearingLayout.editText?.setText(headingTransformation.transform(it.currentBearing))
                overallBearingLayout.editText?.setText(headingTransformation.transform(it.overallBearing))
            }
        }
        viewModel.noOfSatellites.observe(viewLifecycleOwner) {
            satellitesLayout.editText?.setText(it.toString())
        }
    }

    private fun unbindLiveData() {
        activityViewModel.geoTrackServiceReference.removeObservers(viewLifecycleOwner)
        activityViewModel.geoTrackServiceReference.value?.liveUpdate?.removeObservers(
            viewLifecycleOwner
        )
    }

    private fun startTrackClick() {
        LogEx.i(Constants.TAG_CURRENT_TRACK_FRAGMENT, "tracking started")
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
        LogEx.i(Constants.TAG_CURRENT_TRACK_FRAGMENT, "tracking stopped")
        unbindLiveData()
        viewModel.stopTrack(activityViewModel.geoTrackServiceReference)
    }

    // companion -----------------------------------------------------------------------------------

    companion object {
        fun newInstance() = CurrentTrackFragment()
    }
}
