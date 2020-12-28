package ns.fajnet.android.geobreadcrumbs.activities.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.activities.settings.SettingsActivity
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.common.LogEx
import ns.fajnet.android.geobreadcrumbs.services.GeoTrackService

class MainActivity : AppCompatActivity(), ServiceConnection {

    // members -------------------------------------------------------------------------------------

    private val viewModel: MainActivityViewModel by viewModels()

    // overrides -----------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bind()
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, GeoTrackService::class.java)
        bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    override fun onPause() {
        super.onPause()
        unbindService(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_exit -> {
                val intent = Intent(this, GeoTrackService::class.java)
                stopService(intent)
                finishAndRemoveTask()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ServiceConnection ---------------------------------------------------------------------------

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as GeoTrackService.MyBinder
        viewModel.setGeoTrackServiceReference(binder.service)
        LogEx.d(Constants.TAG_MAIN_ACTIVITY, "connected to service")
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        viewModel.unsetGeoTrackServiceReference()
        LogEx.d(Constants.TAG_MAIN_ACTIVITY, "disconnected from service")
    }

    // private methods -----------------------------------------------------------------------------

    private fun bind() {
        setSupportActionBar(toolbar)
        val pagerAdapter = MainActivityPagerAdapter(this, supportFragmentManager)
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = 2
        tabs.setupWithViewPager(viewPager)
    }
}
