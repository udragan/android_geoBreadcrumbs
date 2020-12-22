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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.fragment_live_gps.*
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.Utils
import ns.fajnet.android.geobreadcrumbs.common.displayTransformations.*
import ns.fajnet.android.geobreadcrumbs.common.logger.LogEx

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
                        subscribeToLocationUpdates()
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
                    subscribeToLocationUpdates()
                }
            }
        }
    }

    // HasDefaultViewModelProviderFactory ----------------------------------------------------------

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory {
        return LiveGPSFragmentViewModel.FACTORY(requireActivity().application)
    }

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
        coordinateTransformation = CoordinateTransformation(requireContext())
        altitudeTransformation = AltitudeTransformation(requireContext())
        accuracyTransformation = AccuracyTransformation(requireContext())
        speedTransformation = SpeedTransformation(requireContext())
        bearingTransformation = BearingTransformation()
    }

    private fun bindLiveData() {
        // TODO: use transformations on all displayed values
        viewModel.longitude.observe(viewLifecycleOwner) { value ->
            longitudeLayout.editText!!.setText(
                coordinateTransformation.transform(
                    value,
                    Orientation.HORIZONTAL
                )
            )
        }
        viewModel.latitude.observe(viewLifecycleOwner) { value ->
            latitudeLayout.editText!!.setText(
                coordinateTransformation.transform(
                    value,
                    Orientation.VERTICAL
                )
            )
        }
        viewModel.altitude.observe(viewLifecycleOwner) { value ->
            altitudeLayout.editText!!.setText(altitudeTransformation.transform(value))
        }
        viewModel.locationFixTime.observe(viewLifecycleOwner) { value ->
            locationFixTimeLayout.editText!!.setText(value)
        }
        viewModel.accuracy.observe(viewLifecycleOwner) { value ->
            accuracyLayout.editText!!.setText(accuracyTransformation.transform(value))
        }
        viewModel.speed.observe(viewLifecycleOwner) { value ->
            speedLayout.editText!!.setText(speedTransformation.transform(value))
        }
        viewModel.bearing.observe(viewLifecycleOwner) { value ->
            bearingLayout.editText!!.setText(bearingTransformation.transform(value))
        }
    }

    @SuppressLint("MissingPermission")
    private fun subscribeToLocationUpdates() {
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
