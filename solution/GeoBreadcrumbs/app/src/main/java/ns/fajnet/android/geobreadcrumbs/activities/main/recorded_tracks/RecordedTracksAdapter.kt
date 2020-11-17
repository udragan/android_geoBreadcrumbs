package ns.fajnet.android.geobreadcrumbs.activities.main.recorded_tracks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_view_item_recorded_tracks.view.*
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.database.Track

class RecordedTracksAdapter(private val dataSet: Array<Track>) :
    RecyclerView.Adapter<RecordedTracksAdapter.ViewHolder>() {

    // overrides -----------------------------------------------------------------------------------

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recycler_view_item_recorded_tracks, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.trackName.text = dataSet[position].name
    }

    override fun getItemCount() = dataSet.size

    // =============================================================================================

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val trackName: TextView = view.track_name
    }
}
