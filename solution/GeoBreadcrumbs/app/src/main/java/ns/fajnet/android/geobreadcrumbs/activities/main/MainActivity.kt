package ns.fajnet.android.geobreadcrumbs.activities.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

import ns.fajnet.android.geobreadcrumbs.R

class MainActivity : AppCompatActivity() {

    // members #####################################################################################

    private lateinit var toolbar: Toolbar
    private lateinit var tabs: TabLayout
    private lateinit var viewPager: ViewPager

    // overrides ###################################################################################

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViews()
        bind()
    }

    // private methods #############################################################################

    private fun findViews() {
        toolbar = findViewById(R.id.toolbar)
        tabs = findViewById(R.id.tabs)
        viewPager = findViewById(R.id.view_pager)
    }

    private fun bind() {
        val pagerAdapter = MainActivityPagerAdapter(this, supportFragmentManager)
        viewPager.adapter = pagerAdapter
        tabs.setupWithViewPager(viewPager)
    }
}
