package ns.fajnet.android.geobreadcrumbs.activities.main.current_track

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ns.fajnet.android.geobreadcrumbs.R
import ns.fajnet.android.geobreadcrumbs.common.Constants
import ns.fajnet.android.geobreadcrumbs.services.GeoTrackService

class CurrentTrackFragment : Fragment() {

    // members -------------------------------------------------------------------------------------

    private lateinit var fragmentView: View
    private lateinit var viewModel: CurrentTrackFragmentViewModel

    private lateinit var startTrack: AppCompatButton
    private lateinit var stopTrack: AppCompatButton


    // overrides -----------------------------------------------------------------------------------

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_current_track, container, false)
        findViews()
        bind()

        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CurrentTrackFragmentViewModel::class.java)
        // TODO: Use the ViewModel
    }

    // private methods -----------------------------------------------------------------------------

    private fun findViews() {
        startTrack = fragmentView.findViewById(R.id.start_track)
        stopTrack = fragmentView.findViewById(R.id.stop_track)
    }

    private fun bind() {
        startTrack.setOnClickListener {
            startTrackClick()
        }
        stopTrack.setOnClickListener {
            stopTrackClick()
        }
    }

    private fun startTrackClick() {
        Log.d(Constants.TAG_GEO_TRACK_SERVICE, "start track clicked")

        val intent = Intent(requireContext(), GeoTrackService::class.java)
        ContextCompat.startForegroundService(requireContext(), intent)
    }

    private fun stopTrackClick() {
        Log.d(Constants.TAG_GEO_TRACK_SERVICE, "stop track clicked")

        val intent = Intent(requireContext(), GeoTrackService::class.java)
        requireActivity().stopService(intent)
    }

    // companion -----------------------------------------------------------------------------------

    companion object {
        fun newInstance() = CurrentTrackFragment()
    }
}
