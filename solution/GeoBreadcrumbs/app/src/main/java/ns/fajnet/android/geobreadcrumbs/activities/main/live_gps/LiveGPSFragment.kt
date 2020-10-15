package ns.fajnet.android.geobreadcrumbs.activities.main.live_gps

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputLayout
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.common.Utils

/**
 * A simple [Fragment] subclass.
 * Use the [LiveGPSFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LiveGPSFragment : Fragment() {

    // members #####################################################################################

    private lateinit var fragmentView: View
    private lateinit var longitudeLayout: TextInputLayout
    private lateinit var latitudeLayout: TextInputLayout
    private lateinit var altitudeLayout: TextInputLayout
    private lateinit var elapsedRealTimeNanosLayout: TextInputLayout
    private lateinit var accuracyLayout: TextInputLayout
    private lateinit var speedLayout: TextInputLayout
    private lateinit var bearingLayout: TextInputLayout

    // overrides ###################################################################################

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.fragment_live_gps, container, false)
        findViews()

        return fragmentView
    }

    override fun onStart() {
        super.onStart()
        when {
            Utils.isPermissionGranted(this.requireContext()) -> {
                when {
                    Utils.isLocationEnabled(this.requireContext()) -> {
                        setUpLocationListener()
                    }
                    else -> {
                        //Utils.showGPSNotEnabledDialog(this)
                    }
                }
            }
            else -> {
                Utils.requestLocationPermission(
                    this.requireActivity(),
                    1000
                )
            }
        }
    }

    // private methods #############################################################################

    private fun findViews() {
        longitudeLayout = fragmentView.findViewById(R.id.longitude_layout)
        latitudeLayout = fragmentView.findViewById(R.id.latitude_layout)
        altitudeLayout = fragmentView.findViewById(R.id.altitude_layout)
        elapsedRealTimeNanosLayout = fragmentView.findViewById(R.id.time_layout)
        accuracyLayout = fragmentView.findViewById(R.id.accuracy_layout)
        speedLayout = fragmentView.findViewById(R.id.speed_layout)
        bearingLayout = fragmentView.findViewById(R.id.bearing_layout)
    }

    @SuppressLint("MissingPermission")
    private fun setUpLocationListener(){
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireContext())
        val locationRequest = LocationRequest().setInterval(2000)
            .setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
            object: LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)

                    for (location in locationResult.locations) {
                        longitudeLayout.editText!!.setText(location.longitude.toString())
                        latitudeLayout.editText!!.setText(location.latitude.toString())
                        altitudeLayout.editText!!.setText(location.altitude.toString())
                        elapsedRealTimeNanosLayout.editText!!.setText(location.elapsedRealtimeNanos.toString())
                        accuracyLayout.editText!!.setText(location.accuracy.toString())
                        speedLayout.editText!!.setText(location.speed.toString())
                        bearingLayout.editText!!.setText(location.bearing.toString())
                    }
                }
            },
            Looper.myLooper()
        )
    }

    // companion ###################################################################################

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment LiveGPSFragment.
         */
        @JvmStatic
        fun newInstance() =
            LiveGPSFragment()
    }
}