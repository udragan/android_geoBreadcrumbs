package ns.fajnet.android.geobreadcrumbs.activities.main.live_gps

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import com.google.android.material.textfield.TextInputLayout
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.CoordinateDisplayTransformation
import ns.fajnet.android.geobreadcrumbs.common.Orientation
import ns.fajnet.android.geobreadcrumbs.common.Utils
import java.text.SimpleDateFormat

/**
 * A simple [Fragment] subclass.
 * Use the [LiveGPSFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LiveGPSFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    // members -------------------------------------------------------------------------------------

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var coordinateDisplayTransformation: CoordinateDisplayTransformation

    private lateinit var fragmentView: View
    private lateinit var longitudeLayout: TextInputLayout
    private lateinit var latitudeLayout: TextInputLayout
    private lateinit var altitudeLayout: TextInputLayout
    private lateinit var locationFixTimeLayout: TextInputLayout
    private lateinit var accuracyLayout: TextInputLayout
    private lateinit var speedLayout: TextInputLayout
    private lateinit var bearingLayout: TextInputLayout

    // overrides -----------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readExistingPreferences()
        registerPreferenceChangeListener()
        initialize()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_live_gps, container, false)
        findViews()

        return fragmentView
    }

    override fun onStart() {
        super.onStart()
        Log.d(Constants.TAG_LIVE_GPS_FRAGMENT, "OnStart")

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
        Log.d(Constants.TAG_LIVE_GPS_FRAGMENT, "onStop")
        unsubscribeFromLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(Constants.TAG_LIVE_GPS_FRAGMENT, "onDestroy")
        unregisterPreferenceChangeListener()
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

    // OnSharedPreferencesChangedListener ----------------------------------------------------------

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == getString(R.string.settings_preference_coordinates_display_key)) {
            val defaultValue = resources.getStringArray(R.array.coordinates_display_values)[0]
            val preferenceValue = sharedPreferences
                .getString(
                    getString(R.string.settings_preference_coordinates_display_key),
                    defaultValue
                )
            coordinateDisplayTransformation.transformToDegMinSec = preferenceValue != defaultValue
        }
    }

    // private methods -----------------------------------------------------------------------------

    private fun initialize() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                Log.d(Constants.TAG_LIVE_GPS_FRAGMENT, "locationCallbackTriggered")

                val simpleDateFormat = SimpleDateFormat.getTimeInstance()

                for (location in locationResult.locations) {
                    longitudeLayout.editText!!.setText(
                        coordinateDisplayTransformation.transform(
                            location.longitude,
                            Orientation.HORIZONTAL
                        )
                    )
                    latitudeLayout.editText!!.setText(
                        coordinateDisplayTransformation.transform(
                            location.latitude,
                            Orientation.VERTICAL
                        )
                    )
                    altitudeLayout.editText!!.setText(location.altitude.toString())
                    locationFixTimeLayout.editText!!.setText(
                        simpleDateFormat.format(
                            location.time
                        )
                    )
                    accuracyLayout.editText!!.setText(location.accuracy.toString())
                    speedLayout.editText!!.setText(location.speed.toString())
                    bearingLayout.editText!!.setText(location.bearing.toString())
                }
            }
        }
    }

    private fun readExistingPreferences() {
        val defaultCoordinateDisplayPreference =
            resources.getStringArray(R.array.coordinates_display_values)[0]
        val coordinateDisplayPreference =
            PreferenceManager.getDefaultSharedPreferences(this.requireContext())
                .getString(
                    getString(R.string.settings_preference_coordinates_display_key),
                    defaultCoordinateDisplayPreference
                )
        coordinateDisplayTransformation =
            CoordinateDisplayTransformation(coordinateDisplayPreference != defaultCoordinateDisplayPreference)
    }

    private fun registerPreferenceChangeListener() {
        PreferenceManager.getDefaultSharedPreferences(this.requireContext())
            .registerOnSharedPreferenceChangeListener(this)
    }

    private fun unregisterPreferenceChangeListener() {
        PreferenceManager.getDefaultSharedPreferences(this.requireContext())
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun findViews() {
        longitudeLayout = fragmentView.findViewById(R.id.longitude_layout)
        latitudeLayout = fragmentView.findViewById(R.id.latitude_layout)
        altitudeLayout = fragmentView.findViewById(R.id.altitude_layout)
        locationFixTimeLayout = fragmentView.findViewById(R.id.location_fix_time_layout)
        accuracyLayout = fragmentView.findViewById(R.id.accuracy_layout)
        speedLayout = fragmentView.findViewById(R.id.speed_layout)
        bearingLayout = fragmentView.findViewById(R.id.bearing_layout)
    }

    @SuppressLint("MissingPermission")
    private fun subscribeToLocationUpdates() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this.requireContext())
        val locationRequest = LocationRequest().setInterval(5000)
            .setFastestInterval(5000)
            .setSmallestDisplacement(20f)
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
        fun newInstance() =
            LiveGPSFragment()
    }
}
