package ns.fajnet.android.geobreadcrumbs.activities.main.recorded_tracks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_view_item_recorded_tracks.view.*
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.common.displayTransformations.DistanceTransformation
import ns.fajnet.android.geobreadcrumbs.common.displayTransformations.HeadingTransformation
import ns.fajnet.android.geobreadcrumbs.common.displayTransformations.SpeedTransformation
import ns.fajnet.android.geobreadcrumbs.database.Track

// TODO: think about introducing a TrackModel instead of raw Track (to do calculations only once!)
class RecordedTracksAdapter(context: Context, private val dataSet: Array<Track>) :
    RecyclerView.Adapter<RecordedTracksAdapter.ViewHolder>() {

    // members -------------------------------------------------------------------------------------
    
    private val distanceTransformation = DistanceTransformation(context)
    private val speedTransformation = SpeedTransformation(context)
    private val headingTransformation = HeadingTransformation()

    // overrides -----------------------------------------------------------------------------------

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // TODO: transformations are not applied when settings are changed!!
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recycler_view_item_recorded_tracks, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val track = dataSet[position]
        viewHolder.name.text = track.name
        viewHolder.duration.text = (track.startTimeMillis).toString() // TODO: calculate duration
        viewHolder.distance.text = distanceTransformation.transform(track.distance)
        viewHolder.avgSpeed.text =
            speedTransformation.transform(track.averageSpeed)
        viewHolder.maxSpeed.text =
            speedTransformation.transform(track.maxSpeed)
        viewHolder.places.text = track.numberOfPlaces.toString()
        viewHolder.points.text = track.numberOfPoints.toString()
        viewHolder.bearing.text = headingTransformation.transform(track.bearing)
    }

    override fun getItemCount() = dataSet.size

    // =============================================================================================

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: AppCompatTextView = view.name
        val duration: AppCompatTextView = view.duration
        val distance: AppCompatTextView = view.distance
        val avgSpeed: AppCompatTextView = view.averageSpeed
        val maxSpeed: AppCompatTextView = view.maxSpeed
        val places: AppCompatTextView = view.places
        val points: AppCompatTextView = view.points
        val bearing: AppCompatTextView = view.bearing
    }
}
