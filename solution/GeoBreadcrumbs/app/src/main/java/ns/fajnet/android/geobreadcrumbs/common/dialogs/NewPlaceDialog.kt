package ns.fajnet.android.geobreadcrumbs.common.dialogs

import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.dialog_new_place.view.*
import ns.fajnet.android.geobreadcrumbs.R


class NewPlaceDialog(private val lastLocation: Location?,
                     private val positive: (name: String) -> Unit,
                     private val negative: () -> Unit) :
    DialogFragment() {

    // members -------------------------------------------------------------------------------------

    private var staleNanos = 10000000000

    // overrides -----------------------------------------------------------------------------------

    override fun onCreateDialog(savedInstanceState: Bundle?) = activity?.let {
        val builder = AlertDialog.Builder(this@NewPlaceDialog.requireContext())
        val inflater = requireActivity().layoutInflater
        val contentView = inflater.inflate(
            R.layout.dialog_new_place,
            null
        )
        staleNanos = readExistingPreference()

        when {
            lastLocation == null ||
                    staleNanos > 0 &&
                    System.nanoTime() - lastLocation.elapsedRealtimeNanos > staleNanos -> {
                contentView.location_stale.visibility = View.VISIBLE
            }
        }

        builder.setView(contentView)
            .setMessage(R.string.current_track_dialog_new_place_title)
            .setPositiveButton(R.string.dialog_button_ok) { _, _ ->
                val name = contentView.placeName.editText?.text.toString()
                positive.invoke(name)
            }
            .setNegativeButton(R.string.dialog_button_cancel) { _, _ ->
                negative.invoke()
            }
        val dialog = builder.create()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        dialog
    } ?: throw  IllegalStateException("Activity cannot be null!")

    // private methods -----------------------------------------------------------------------------

    private fun readExistingPreference(): Long {
        val defaultValue =
            context?.resources?.getStringArray(R.array.last_known_location_values)?.get(1)

        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(
                context?.getString(R.string.settings_preference_last_known_location_stale_key),
                defaultValue
            )
            ?.toLong()!!
    }
}
