package ns.fajnet.android.geobreadcrumbs.activities.main.recorded_tracks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_view_item_recorded_tracks.view.*
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.database.Track

// TODO: think about introducing a TrackModel instead of raw Track (to do calculations only once!)
class RecordedTracksAdapter(private val dataSet: Array<Track>) :
    RecyclerView.Adapter<RecordedTracksAdapter.ViewHolder>() {

    // overrides -----------------------------------------------------------------------------------

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recycler_view_item_recorded_tracks, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val track = dataSet[position]
        viewHolder.name.text = track.name
        viewHolder.duration.text = (track.startTimeMillis).toString() // TODO: calculate duration
        viewHolder.length.text = 100.toString() // TODO: calculate length based on points
        viewHolder.avgSpeed.text =
            track.averageSpeed.toString() // TODO: add trailing unit (when settings for measurement is added)
        viewHolder.maxSpeed.text =
            track.maxSpeed.toString() // TODO: add trailing unit (when settings for measurement is added)
        viewHolder.places.text = track.numberOfPlaces.toString()
        viewHolder.points.text = track.numberOfPoints.toString()
        viewHolder.bearing.text = track.bearing.toString() // TODO: convert to direction
    }

    override fun getItemCount() = dataSet.size

    // =============================================================================================

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: AppCompatTextView = view.name
        val duration: AppCompatTextView = view.duration
        val length: AppCompatTextView = view.length
        val avgSpeed: AppCompatTextView = view.average_speed
        val maxSpeed: AppCompatTextView = view.max_speed
        val places: AppCompatTextView = view.places
        val points: AppCompatTextView = view.points
        val bearing: AppCompatTextView = view.bearing
    }
}
