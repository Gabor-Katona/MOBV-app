package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.mapbox.maps.MapView
import com.mapbox.maps.Style

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var mapView: MapView? = null

    private lateinit var viewModel: FeedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var mapView: MapView? = view.findViewById(R.id.mapView)
        mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)

        view.findViewById<Button>(R.id.change_passwd_button).apply {
            setOnClickListener {
                it.findNavController().navigate(R.id.action_to_changePassword)
            }
        }

        viewModel = ViewModelProvider(requireActivity())[FeedViewModel::class.java]

        viewModel.feed_items.observe(viewLifecycleOwner) { items ->
            // Tu môžete aktualizovať UI podľa hodnoty stringValue
            Log.d("Profile", "prvky su $items")
        }



    }
}