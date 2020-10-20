package ns.fajnet.android.geobreadcrumbs.activities.settings

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.activities.settings.preferences.SettingsFragment


class SettingsActivity : AppCompatActivity() {

    // members -------------------------------------------------------------------------------------

    private lateinit var toolbar: Toolbar

    // overrides -----------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }

        findViews()
        bind()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this)
        }

        return super.onOptionsItemSelected(item)
    }

    // private methods -----------------------------------------------------------------------------

    private fun findViews() {
        toolbar = findViewById(R.id.toolbar)
    }

    private fun bind() {
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
