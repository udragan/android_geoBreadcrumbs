package ns.fajnet.android.geobreadcrumbs.common

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object Utils {
    fun requestLocationPermission(activity: Activity,
                                  requestId: Int) {
        val permissionsToRequest = if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        ActivityCompat.requestPermissions(activity,
            permissionsToRequest,
            requestId
        )
    }

    fun isPermissionGranted(context: Context): Boolean {
        val permissionStatus = ContextCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_FINE_LOCATION)

        return permissionStatus == PackageManager.PERMISSION_GRANTED
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
               locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Function to show the "enable GPS" Dialog box
     */
//    fun showGPSNotEnabledDialog(context: Context) {
//        AlertDialog.Builder(context)
//            .setTitle(context.getString(R.string.enable_gps))
//            .setMessage(context.getString(R.string.required_for_this_app))
//            .setCancelable(false)
//            .setPositiveButton(context.getString(R.string.enable_now)) { _, _ ->
//                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
//            }
//            .show()
//    }
}
