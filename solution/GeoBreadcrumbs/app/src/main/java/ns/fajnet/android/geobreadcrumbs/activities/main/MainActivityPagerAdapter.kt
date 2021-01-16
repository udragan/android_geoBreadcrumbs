package ns.fajnet.android.geobreadcrumbs.activities.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.activities.main.current_track.CurrentTrackFragment
import ns.fajnet.android.geobreadcrumbs.activities.main.live_gps.LiveGPSFragment
import ns.fajnet.android.geobreadcrumbs.activities.main.recorded_tracks.RecordedTracksFragment

private val TAB_TITLES = arrayOf(
    R.string.live_gps_tab_text,
    R.string.current_track_tab_text,
    R.string.recorded_tracks_tab_text
)

class MainActivityPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    // members -------------------------------------------------------------------------------------

    private var fragments = arrayOf<Fragment>()

    // constructors / init -------------------------------------------------------------------------

    init {
        val liveGpsFragment = LiveGPSFragment.newInstance()
        val currentTrackFragment = CurrentTrackFragment.newInstance()
        val recordedTracksFragment = RecordedTracksFragment.newInstance()
        fragments = arrayOf(liveGpsFragment, currentTrackFragment, recordedTracksFragment)
    }

    // overrides -----------------------------------------------------------------------------------

    override fun getItem(position: Int) = fragments[position]

    override fun getPageTitle(position: Int) =
        context.resources.getString(TAB_TITLES[position])

    override fun getCount() = 3
}
