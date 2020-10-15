package ns.fajnet.android.geobreadcrumbs.activities.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.activities.main.live_gps.LiveGPSFragment

private val TAB_TITLES = arrayOf(
    R.string.live_gps_tab_text
)

class MainActivityPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    // members #####################################################################################

    private var fragments = arrayOf<Fragment>()

    // constructors / init #########################################################################

    init {
        val liveGpsFragment = LiveGPSFragment.newInstance()
        fragments = arrayOf(liveGpsFragment)
    }

    // overrides ###################################################################################

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 1
    }
}
