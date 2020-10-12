package ns.fajnet.android.geobreadcrumbs.activities.main.live_gps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ns.fajnet.android.geobreadcrumbs.R

/**
 * A simple [Fragment] subclass.
 * Use the [LiveGPSFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LiveGPSFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_live_gps, container, false)
    }

    // companion ##############################################
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment LiveGPSFragment.
         */
        @JvmStatic
        fun newInstance() =
            LiveGPSFragment()
    }
}