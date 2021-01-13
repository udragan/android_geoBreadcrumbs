package ns.fajnet.android.geobreadcrumbs.common.dialogs

import android.app.Dialog
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_new_place.view.*
import ns.fajnet.android.geobreadcrumbs.R


class NewPlaceDialog(private val lastLocation: Location?,
                     private val positive: (name: String) -> Unit,
                     private val negative: () -> Unit) :
    DialogFragment() {

    // overrides -----------------------------------------------------------------------------------

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(this@NewPlaceDialog.requireContext())
            val inflater = requireActivity().layoutInflater
            val contentView = inflater.inflate(
                R.layout.dialog_new_place,
                null
            )

            when { // TODO: get stale time from preferences!
                lastLocation == null || System.nanoTime() - lastLocation.elapsedRealtimeNanos > 10000 -> {
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
            builder.create()
        } ?: throw  IllegalStateException("Activity cannot be null!")
    }
}
