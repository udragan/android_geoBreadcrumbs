package ns.fajnet.android.geobreadcrumbs.activities.main.live_gps

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.fragment_live_gps.*
import kotlinx.coroutines.launch
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.LogEx
import ns.fajnet.android.geobreadcrumbs.common.Utils
import ns.fajnet.android.geobreadcrumbs.common.displayTransformations.*


/**
 * A simple [Fragment] subclass.
 * Use the [LiveGPSFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LiveGPSFragment : Fragment(), HasDefaultViewModelProviderFactory {

    // members -------------------------------------------------------------------------------------

    private val viewModel: LiveGPSFragmentViewModel by viewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var coordinateTransformation: CoordinateTransformation
    private lateinit var altitudeTransformation: AltitudeTransformation
    private lateinit var accuracyTransformation: AccuracyTransformation
    private lateinit var speedTransformation: SpeedTransformation
    private lateinit var bearingTransformation: BearingTransformation
    private lateinit var signalQualityTransformation: SignalQualityTransformation

    // overrides -----------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogEx.d(Constants.TAG_LIVE_GPS_FRAGMENT, "onCreate")
        initialize()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_live_gps, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bind()
        bindLiveData()
    }

    override fun onStart() {
        super.onStart()
        LogEx.d(Constants.TAG_LIVE_GPS_FRAGMENT, "OnStart")

        when {
            Utils.isPermissionGranted(this.requireContext()) -> {
                when {
                    Utils.isLocationEnabled(this.requireContext()) -> {
                        // TODO: uncomment
                        requestGpsUpdates()
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

    override fun onStop() {
        super.onStop()
        LogEx.d(Constants.TAG_LIVE_GPS_FRAGMENT, "onStop")
        unsubscribeFromLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        LogEx.d(Constants.TAG_LIVE_GPS_FRAGMENT, "onDestroy")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.REQUEST_CODE_REQUEST_LOCATION_PERMISSION -> {
                if (grantResults[0] == 0) {
                    requestGpsUpdates()
                }
            }
        }
    }

    // HasDefaultViewModelProviderFactory ----------------------------------------------------------

    override fun getDefaultViewModelProviderFactory() =
        LiveGPSFragmentViewModel.FACTORY(requireActivity().application)

    // private methods -----------------------------------------------------------------------------

    private fun initialize() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                LogEx.d(Constants.TAG_LIVE_GPS_FRAGMENT, "locationCallbackTriggered")

                for (location in locationResult.locations) {
                    viewModel.setLocation(location)
                }
            }
        }
    }

    private fun bind() {
        lifecycleScope.launch {
            coordinateTransformation = CoordinateTransformation(requireContext())
            altitudeTransformation = AltitudeTransformation(requireContext())
            accuracyTransformation = AccuracyTransformation(requireContext())
            speedTransformation = SpeedTransformation(requireContext())
            bearingTransformation = BearingTransformation()
            signalQualityTransformation = SignalQualityTransformation(requireContext())
        }
    }

    private fun bindLiveData() {
        // TODO: use transformations on all displayed values
        viewModel.longitude.observe(viewLifecycleOwner) {
            longitudeLayout.editText!!.setText(
                coordinateTransformation.transform(
                    it,
                    Orientation.HORIZONTAL
                )
            )
        }
        viewModel.latitude.observe(viewLifecycleOwner) {
            latitudeLayout.editText!!.setText(
                coordinateTransformation.transform(
                    it,
                    Orientation.VERTICAL
                )
            )
        }
        viewModel.altitude.observe(viewLifecycleOwner) {
            altitudeLayout.editText!!.setText(altitudeTransformation.transform(it))
        }
        viewModel.locationFixTime.observe(viewLifecycleOwner) {
            locationFixTimeLayout.editText!!.setText(it)
        }
        viewModel.accuracy.observe(viewLifecycleOwner) {
            accuracyLayout.editText!!.setText(accuracyTransformation.transform(it))
        }
        viewModel.speed.observe(viewLifecycleOwner) {
            speedLayout.editText!!.setText(speedTransformation.transform(it))
        }
        viewModel.bearing.observe(viewLifecycleOwner) {
            bearingLayout.editText!!.setText(bearingTransformation.transform(it))
        }
        viewModel.noOfSatellites.observe(viewLifecycleOwner) {
            satellitesLayout.editText!!.setText(it.toString())
            satellitesLayout.editText!!.setTextColor(signalQualityTransformation.transform(it))
        }
    }

    private fun requestGpsUpdates() {
        requestLocationUpdates()
        requestGpsStatus()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this.requireContext())
        val locationRequest = LocationRequest().setInterval(5000)
            .setFastestInterval(5000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestGpsStatus() {
        viewModel.requestGpsStatus()
    }

    @SuppressLint("MissingPermission")
    private fun unsubscribeFromLocationUpdates() {
        if (this::fusedLocationProviderClient.isInitialized) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    // companion -----------------------------------------------------------------------------------

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment LiveGPSFragment.
         */
        @JvmStatic
        fun newInstance() = LiveGPSFragment()
    }
}
