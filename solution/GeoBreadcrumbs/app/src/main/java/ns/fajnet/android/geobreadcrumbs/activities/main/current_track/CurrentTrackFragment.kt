package ns.fajnet.android.geobreadcrumbs.activities.main.current_track

import android.annotation.SuppressLint
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
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_current_track.*
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.activities.main.MainActivityViewModel
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.LogEx
import ns.fajnet.android.geobreadcrumbs.common.Utils
import ns.fajnet.android.geobreadcrumbs.common.dialogs.NewPlaceDialog
import ns.fajnet.android.geobreadcrumbs.common.displayTransformations.*

class CurrentTrackFragment : Fragment(), HasDefaultViewModelProviderFactory {

    // members -------------------------------------------------------------------------------------

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: CurrentTrackFragmentViewModel by viewModels()
    private lateinit var durationTransformation: DurationTransformation
    private lateinit var distanceTransformation: DistanceTransformation
    private lateinit var speedTransformation: SpeedTransformation
    private lateinit var headingTransformation: HeadingTransformation
    private lateinit var signalQualityTransformation: SignalQualityTransformation

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.REQUEST_CODE_REQUEST_LOCATION_PERMISSION -> {
                if (grantResults[0] == 0) {
                    getLastLocationAndShowDialog()
                }
            }
        }
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
        addPlace.setOnClickListener {
            addPlaceClick()
        }

        durationTransformation = DurationTransformation()
        distanceTransformation = DistanceTransformation(requireContext())
        speedTransformation = SpeedTransformation(requireContext())
        headingTransformation = HeadingTransformation()
        signalQualityTransformation = SignalQualityTransformation(requireContext())
    }

    private fun bindLiveData() {
        activityViewModel.geoTrackServiceReference.observe(viewLifecycleOwner) { value ->
            LogEx.d(Constants.TAG_CURRENT_TRACK_FRAGMENT, "serviceReference updated")
            value.recordingActive.observe(viewLifecycleOwner) {
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
            value.liveUpdate.observe(viewLifecycleOwner) {
                LogEx.d(Constants.TAG_CURRENT_TRACK_FRAGMENT, "liveUpdate received")
                durationLayout.editText?.setText(durationTransformation.transform(it.duration))
                distanceLayout.editText?.setText(distanceTransformation.transform(it.distance))
                currentSpeedLayout.editText?.setText(speedTransformation.transform(it.currentSpeed))
                averageSpeedLayout.editText?.setText(speedTransformation.transform(it.averageSpeed))
                maxSpeedLayout.editText?.setText(speedTransformation.transform(it.maxSpeed))
                currentBearingLayout.editText?.setText(headingTransformation.transform(it.currentBearing))
                overallBearingLayout.editText?.setText(headingTransformation.transform(it.overallBearing))
                noOfPlacesLayout.editText?.setText(it.noOfPlaces.toString())
                noOfPointsLayout.editText?.setText(it.noOfPoints.toString())
            }
        }
        viewModel.noOfSatellites.observe(viewLifecycleOwner) {
            satellitesLayout.editText?.setText(it.toString())
            satellitesLayout.editText?.setTextColor(signalQualityTransformation.transform(it))
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

        when {
            Utils.isPermissionGranted(this.requireContext()) -> {
                when {
                    Utils.isLocationEnabled(this.requireContext()) -> {
                        getLastLocationAndShowDialog()
                    }
                    else -> {
                        //Utils.showGPSNotEnabledDialog(this)
                    }
                }
            }
            else -> {
                Utils.requestLocationPermission(
                    this,
                    Constants.REQUEST_CODE_REQUEST_LOCATION_PERMISSION
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocationAndShowDialog() {
        indeterminateProgressBar.visibility = View.VISIBLE
        LocationServices.getFusedLocationProviderClient(requireContext())
            .lastLocation
            .addOnSuccessListener {
                LogEx.i(Constants.TAG_CURRENT_TRACK_FRAGMENT, "received last location: $it")
                NewPlaceDialog(
                    it,
                    { placeName ->
                        LogEx.i(Constants.TAG_CURRENT_TRACK_FRAGMENT, "dialog OK: $placeName")
                        viewModel.startTrack(placeName)
                    },
                    {
                        LogEx.i(Constants.TAG_CURRENT_TRACK_FRAGMENT, "dialog Cancel")
                    }
                ).show(childFragmentManager, "newPoint")
                unbindLiveData()
                bindLiveData()
            }
            .addOnCompleteListener {
                indeterminateProgressBar.visibility = View.GONE
            }
    }

    private fun stopTrackClick() {
        LogEx.i(Constants.TAG_CURRENT_TRACK_FRAGMENT, "tracking stopped")
        unbindLiveData()
        viewModel.stopTrack(activityViewModel.geoTrackServiceReference)
    }

    @SuppressLint("MissingPermission")
    private fun addPlaceClick() {
        LogEx.i(Constants.TAG_CURRENT_TRACK_FRAGMENT, "add place to current track")
        indeterminateProgressBar.visibility = View.VISIBLE
        LocationServices.getFusedLocationProviderClient(requireContext())
            .lastLocation
            .addOnSuccessListener {
                LogEx.i(Constants.TAG_CURRENT_TRACK_FRAGMENT, "received last location: $it")
                NewPlaceDialog(
                    it,
                    { placeName ->
                        LogEx.i(Constants.TAG_CURRENT_TRACK_FRAGMENT, "dialog OK: $placeName")
                        viewModel.addPlace(
                            activityViewModel.geoTrackServiceReference,
                            placeName,
                            it
                        )
                    },
                    {
                        LogEx.i(Constants.TAG_CURRENT_TRACK_FRAGMENT, "dialog Cancel")
                    }
                ).show(childFragmentManager, "newPoint")
                unbindLiveData()
                bindLiveData()
            }
            .addOnCompleteListener {
                indeterminateProgressBar.visibility = View.GONE
            }
    }

    // companion -----------------------------------------------------------------------------------

    companion object {
        fun newInstance() = CurrentTrackFragment()
    }
}
