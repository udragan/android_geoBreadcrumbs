package ns.fajnet.android.geobreadcrumbs.common.dialogs

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_new_place.view.*
import kotlinx.android.synthetic.main.dialog_rename_track.*
import kotlinx.android.synthetic.main.dialog_rename_track.view.*
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.database.Track


class RenameTrackDialog(private val track: Track,
                        private val positive: (name: String) -> Unit) :
    DialogFragment() {

    // overrides -----------------------------------------------------------------------------------

    override fun onCreateDialog(savedInstanceState: Bundle?) = activity?.let {
        val builder = AlertDialog.Builder(this@RenameTrackDialog.requireContext())
        val inflater = requireActivity().layoutInflater
        val contentView = inflater.inflate(
            R.layout.dialog_rename_track,
            null
        )

        contentView.trackName.editText?.setText(track.name)
        contentView.trackName.editText?.selectAll()

        builder.setView(contentView)
            .setMessage(R.string.recorded_tracks_dialog_rename_track_title)
            .setPositiveButton(R.string.dialog_button_ok) { _, _ ->
                val name = contentView.trackName.editText?.text.toString()
                positive.invoke(name)
            }
            .setNegativeButton(R.string.dialog_button_cancel) { _, _ -> }
        val dialog = builder.create()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        dialog
    } ?: throw  IllegalStateException("Activity cannot be null!")
}
