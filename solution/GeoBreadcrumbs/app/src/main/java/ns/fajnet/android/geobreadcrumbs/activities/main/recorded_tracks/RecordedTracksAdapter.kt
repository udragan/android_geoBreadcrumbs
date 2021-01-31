package ns.fajnet.android.geobreadcrumbs.activities.main.recorded_tracks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_view_item_recorded_tracks.view.*
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.LogEx
import ns.fajnet.android.geobreadcrumbs.common.displayTransformations.DistanceTransformation
import ns.fajnet.android.geobreadcrumbs.common.displayTransformations.DurationTransformation
import ns.fajnet.android.geobreadcrumbs.common.displayTransformations.HeadingTransformation
import ns.fajnet.android.geobreadcrumbs.common.displayTransformations.SpeedTransformation
import ns.fajnet.android.geobreadcrumbs.database.Track

// TODO: think about introducing a TrackModel instead of raw Track (to do calculations only once!)
class RecordedTracksAdapter(context: Context) : BaseAdapter() {

    // members -------------------------------------------------------------------------------------

    private val durationTransformation = DurationTransformation()
    private val distanceTransformation = DistanceTransformation(context)
    private val speedTransformation = SpeedTransformation(context)
    private val headingTransformation = HeadingTransformation()
    private val settingsChangedHandler = {
        this.notifyDataSetChanged()
        LogEx.d(Constants.TAG_RECORDED_TRACKS_ADAPTER, "preferences changed, rebinding viewHolders")
    }

    // init / constructors -------------------------------------------------------------------------

    init {
        distanceTransformation.subscribe(settingsChangedHandler)
        speedTransformation.subscribe(settingsChangedHandler)
    }

    // properties ----------------------------------------------------------------------------------

    var dataSet: Array<Track> = arrayOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    // overrides -----------------------------------------------------------------------------------

    override fun getCount(): Int = dataSet.size

    override fun getItem(position: Int): Any = dataSet[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.list_view_item_recorded_tracks, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = convertView.tag as ViewHolder
        }

        val track = getItem(position) as Track

        viewHolder.name.text = track.name
        viewHolder.duration.text =
            durationTransformation.transform(track.endTimeMillis - track.startTimeMillis)
        viewHolder.distance.text = distanceTransformation.transform(track.distance)
        viewHolder.avgSpeed.text =
            speedTransformation.transform(track.averageSpeed)
        viewHolder.maxSpeed.text =
            speedTransformation.transform(track.maxSpeed)
        viewHolder.bearing.text = headingTransformation.transform(track.bearing)
        viewHolder.places.text = track.numberOfPlaces.toString()
        viewHolder.points.text = track.numberOfPoints.toString()

        return view
    }

    // public methods ------------------------------------------------------------------------------

    fun detach() {
        durationTransformation.dispose()
        distanceTransformation.dispose()
        speedTransformation.dispose()
        headingTransformation.dispose()
    }

    // =============================================================================================

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: AppCompatTextView = view.name
        val duration: AppCompatTextView = view.duration
        val distance: AppCompatTextView = view.distance
        val avgSpeed: AppCompatTextView = view.averageSpeed
        val maxSpeed: AppCompatTextView = view.maxSpeed
        val bearing: AppCompatTextView = view.bearing
        val places: AppCompatTextView = view.places
        val points: AppCompatTextView = view.points
    }
}
