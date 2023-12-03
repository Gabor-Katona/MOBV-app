package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import eu.mcomputing.mobv.zadanie.data.api.DataRepository
import eu.mcomputing.mobv.zadanie.databinding.FragmentOtherProfileBinding
import eu.mcomputing.mobv.zadanie.viewmodels.FeedViewModel

class OtherProfileFragment : Fragment(R.layout.fragment_other_profile) {
    private var mapView: MapView? = null
    private lateinit var viewModel: FeedViewModel
    private lateinit var binding: FragmentOtherProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializácia ViewModel
        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[FeedViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtherProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*var mapView: MapView? = view.findViewById(R.id.mapView)
        mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)*/

        viewModel.selectedUser.value?.let { Log.d("otherimport", it.name) }
        viewModel.selectedUser.value?.let { Log.d("otherimport", it.lat.toString()) }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->

            var mapView: MapView? = bnd.mapView
            mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)

            Picasso.get()
                .load("https://upload.mcomputing.eu/" + viewModel.selectedUser.value?.photo )
                .placeholder(R.drawable.ic_action_account)
                .into(bnd.imageView)
        }
    }

}