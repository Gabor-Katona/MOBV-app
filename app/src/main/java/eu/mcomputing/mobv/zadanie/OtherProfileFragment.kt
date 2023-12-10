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
            var annotationManager = bnd.mapView.annotations.createCircleAnnotationManager()

            /*mapView?.getMapboxMap()?.setCamera(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(-98.0, 39.5))
                    .pitch(0.0)
                    .zoom(2.0)
                    .bearing(0.0)
                    .build()
            )*/


            val center = Point.fromLngLat(37.783333, -122.416667)
            val radius = 1000.0 // In meters



            //val point = Point("type", null, listOf(-98.0, 39.5))

            /*mapView?.getMapboxMap()?.loadStyle(
                style(Style.MAPBOX_STREETS) {
                    val pointAnnotationOptions = CircleAnnotationOptions()
                        .withPoint(center)
                        .withCircleRadius(100.0)
                        .withCircleOpacity(0.2)
                        .withCircleColor("#000")
                        .withCircleStrokeWidth(2.0)
                        .withCircleStrokeColor("#ffffff")
                    annotationManager.create(pointAnnotationOptions)
                }
            )*/
            val annotationPlugin = binding.mapView.annotations
            val circleAnnotationManager = annotationPlugin.createCircleAnnotationManager().apply {
                mapView?.getMapboxMap()?.loadStyle(
                    style(Style.MAPBOX_STREETS) {
                        val circleAnnotationOptions: CircleAnnotationOptions =
                            CircleAnnotationOptions()
                                .withPoint(Point.fromLngLat(37.783333, -122.416667))
                                .withCircleColor(Color.YELLOW)
                                .withCircleRadius(12.0)
                                .withDraggable(false)
                        create(circleAnnotationOptions)
                    }
                )
            }



            Picasso.get()
                .load("https://upload.mcomputing.eu/" + viewModel.selectedUser.value?.photo )
                .placeholder(R.drawable.ic_action_account)
                .into(bnd.imageView)
        }
    }

}