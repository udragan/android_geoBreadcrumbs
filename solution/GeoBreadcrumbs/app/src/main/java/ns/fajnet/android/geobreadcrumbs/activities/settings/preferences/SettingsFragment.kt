package ns.fajnet.android.geobreadcrumbs.activities.settings.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ns.fajnet.android.geobreadcrumbs.R

class SettingsFragment : PreferenceFragmentCompat() {

    // overrides -----------------------------------------------------------------------------------

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}
