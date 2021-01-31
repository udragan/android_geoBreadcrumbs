package ns.fajnet.android.geobreadcrumbs.common

import android.view.ActionMode
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.AbsListView
import android.widget.ListView
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.database.Track

class MultiChoiceModeListener(private val listView: ListView) :
    AbsListView.MultiChoiceModeListener {

    // delegates -----------------------------------------------------------------------------------

    lateinit var renameTrackDelegate: (arg: Track) -> Unit

    // overrides -----------------------------------------------------------------------------------

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        val menuInflater: MenuInflater = mode.menuInflater
        menuInflater.inflate(R.menu.menu_recorded_tracks_contextual_action_bar, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        val item = menu.findItem(R.id.action_rename)

        return when {
            listView.checkedItemCount > 1 && item.isVisible -> {
                item.isVisible = false
                true
            }
            listView.checkedItemCount < 2 && !item.isVisible -> {
                item.isVisible = true
                true
            }
            else -> false
        }
    }

    override fun onItemCheckedStateChanged(mode: ActionMode,
                                           position: Int,
                                           id: Long,
                                           checked: Boolean) {

        mode.title = " ${listView.checkedItemCount} selected"
        mode.invalidate()
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem) =
        when (item.itemId) {
            R.id.action_rename -> {
                LogEx.d(Constants.TAG_RECORDED_TRACKS_FRAGMENT, "rename selected")
                val checked = listView.checkedItemPositions

                for (i in 0 until listView.adapter.count) {
                    if (checked[i]) {
                        renameTrack(listView.getItemAtPosition(i))
                        break
                    }
                }

                mode.finish()
                true
            }
            else -> false
        }

    override fun onDestroyActionMode(mode: ActionMode) {
    }

    // private methods -----------------------------------------------------------------------------

    private fun renameTrack(item: Any) {
        val track = item as Track
        renameTrackDelegate.invoke(track)
    }
}
