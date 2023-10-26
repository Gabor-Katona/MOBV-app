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
import eu.mcomputing.mobv.zadanie.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var mapView: MapView? = null
    private var binding: FragmentProfileBinding? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*var mapView: MapView? = view.findViewById(R.id.mapView)
        mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)*/

        binding = FragmentProfileBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->

            var mapView: MapView? = bnd.mapView
            mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)

            bnd.changePasswdButton.setOnClickListener {
                it.findNavController().navigate(R.id.action_to_changePassword)
            }
        }

    }
}