package ns.fajnet.android.geobreadcrumbs.activities.main.current_track

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ns.fajnet.android.geobreadcrumbs.R

class CurrentTrackFragment : Fragment() {

    // members -------------------------------------------------------------------------------------

    private lateinit var viewModel: CurrentTrackFragmentViewModel

    // overrides -----------------------------------------------------------------------------------

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.current_track_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CurrentTrackFragmentViewModel::class.java)
        // TODO: Use the ViewModel
    }

    // companion -----------------------------------------------------------------------------------

    companion object {
        fun newInstance() = CurrentTrackFragment()
    }
}
