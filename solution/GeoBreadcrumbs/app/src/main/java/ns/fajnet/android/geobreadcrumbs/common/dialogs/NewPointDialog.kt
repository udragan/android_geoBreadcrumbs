package ns.fajnet.android.geobreadcrumbs.common.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_new_point.view.*
import ns.fajnet.android.geobreadcrumbs.R


class NewPointDialog(private val positive: (name: String) -> Unit,
                     private val negative: () -> Unit) :
    DialogFragment() {

    // overrides -----------------------------------------------------------------------------------

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(this@NewPointDialog.requireContext())
            val inflater = requireActivity().layoutInflater
            val contentView = inflater.inflate(
                R.layout.dialog_new_point,
                null
            )
            builder.setView(contentView)
                .setMessage(R.string.current_track_dialog_new_point_title)
                .setPositiveButton(R.string.dialog_button_ok) { _, _ ->
                    val name = contentView.point_name.editText?.text.toString()
                    positive.invoke(name)
                }
                .setNegativeButton(R.string.dialog_button_cancel) { _, _ ->
                    negative.invoke()
                }
            builder.create()
        } ?: throw  IllegalStateException("Activity cannot be null!")
    }
}
