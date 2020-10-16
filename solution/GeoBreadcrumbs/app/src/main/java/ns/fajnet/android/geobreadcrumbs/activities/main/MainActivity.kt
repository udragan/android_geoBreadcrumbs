package ns.fajnet.android.geobreadcrumbs.activities.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_settings) {
//            val intent = Intent(this, SettingsActivity::class.java)
//            startActivity(intent)
            Toast.makeText(this, "settings clicked", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // private methods #############################################################################

    private fun findViews() {
        toolbar = findViewById(R.id.toolbar)
        tabs = findViewById(R.id.tabs)
        viewPager = findViewById(R.id.view_pager)
    }

    private fun bind() {
        setSupportActionBar(toolbar)
        val pagerAdapter = MainActivityPagerAdapter(this, supportFragmentManager)
        viewPager.adapter = pagerAdapter
        tabs.setupWithViewPager(viewPager)
    }
}
