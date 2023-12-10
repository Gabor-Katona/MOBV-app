package eu.mcomputing.mobv.zadanie

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.CircleLayer
import com.mapbox.maps.extension.style.layers.generated.circleLayer
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager

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
            //mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)
            val annotationManager = bnd.mapView.annotations.createCircleAnnotationManager()

            Picasso.get()
                .load("https://upload.mcomputing.eu/" + viewModel.selectedUser.value?.photo )
                .placeholder(R.drawable.ic_action_account)
                .into(bnd.imageView)

            var center = Point.fromLngLat(0.0, 0.0)
            center = viewModel.selectedUser.value?.let { Point.fromLngLat(it.lon, it.lat) }

            val radius = if (viewModel.selectedUser.value != null) viewModel.selectedUser.value!!.radius else 100.0

            bnd.mapView.getMapboxMap().loadStyle(
                style(Style.MAPBOX_STREETS) {
                    val pointAnnotationOptions = CircleAnnotationOptions()
                        .withPoint(center)
                        .withCircleRadius(radius)
                        .withCircleOpacity(0.2)
                        .withCircleColor("#000")
                        .withCircleStrokeWidth(2.0)
                        .withCircleStrokeColor("#ffffff")
                    annotationManager.create(pointAnnotationOptions)
                }
            )

            // center the camera
            bnd.mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(center).zoom(15.0).build())
        }
    }

}