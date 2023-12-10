package eu.mcomputing.mobv.zadanie

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotation
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import eu.mcomputing.mobv.zadanie.data.PreferenceData
import eu.mcomputing.mobv.zadanie.databinding.FragmentMapBinding
import org.json.JSONObject
import java.lang.Math.PI
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt


class MapFragment : Fragment() {
    private lateinit var binding: FragmentMapBinding
    private var selectedPoint: CircleAnnotation? = null
    private var lastLocation: Point? = null
    private lateinit var annotationManager: CircleAnnotationManager
    private lateinit var pointAnnotationManager: PointAnnotationManager

    private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                initLocationComponent()
                addLocationListeners()
            }
        }

    fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->

            annotationManager = bnd.mapView.annotations.createCircleAnnotationManager()
            pointAnnotationManager = bnd.mapView.annotations.createPointAnnotationManager()

            // permission check
            val hasPermission = hasPermissions(requireContext())
            val sharing = PreferenceData.getInstance().getSharing(requireContext())
            // map initialization
            onMapReady(hasPermission && sharing)

            bnd.myLocation.setOnClickListener {
                if (!hasPermissions(requireContext())) {
                    // no permission
                    requestPermissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                } else {
                    lastLocation?.let {
                        refreshLocation(it)
                    }
                    addLocationListeners()
                    Log.d("MapFragment","location click")
                }
            }

            /*Glide.with(requireContext())
                .asBitmap()
                .load(R.drawable.ic_action_account)
                .circleCrop()
                .override(65, 65)
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val options = PointAnnotationOptions()
                            .withPoint(Point.fromLngLat(37.783333, -122.416667))
                            .withIconImage(resource)

                        pointAnnotationManager.create(options)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        TODO("Not yet implemented")
                    }
                })*/
        }
    }

    /**
     * Map initialization - sets the camera, loads the style, and adds listeners
     * for map clicks and position changes
     */
    private fun onMapReady(enabled: Boolean) {
        // mapbox camera setup
        binding.mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(14.3539484, 49.8001304))
                .zoom(2.0)
                .build()
        )

        // set map style
        binding.mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            if (enabled) {
                initLocationComponent()
                addLocationListeners()
            }
        }

        // listeners for clicking on the map
        binding.mapView.getMapboxMap().addOnMapClickListener {
            if (hasPermissions(requireContext())) {
                onCameraTrackingDismissed()
            }
            true
        }
    }

    /**
     * Initializes component to retrieve current location
     */
    private fun initLocationComponent() {
        val locationComponentPlugin = binding.mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.pulsingEnabled = true
        }

    }

    /**
     * Adds listeners to track position changes and gestures
     */
    private fun addLocationListeners() {
        binding.mapView.location.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        binding.mapView.gestures.addOnMoveListener(onMoveListener)

    }

    // activated when changes position
    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        Log.d("MapFragment", "poloha je $it")
        // update map with new position
        refreshLocation(it)
    }

    /**
     * Updates map with user's new location
     */
    private fun refreshLocation(point: Point) {
        // center camera
        binding.mapView.getMapboxMap()
            .setCamera(CameraOptions.Builder().center(point).zoom(15.0).build())
        // sets focal point to the new position
        binding.mapView.gestures.focalPoint =
            binding.mapView.getMapboxMap().pixelForCoordinate(point)
        // updates lastLocation
        lastLocation = point
        addMarker(point)
    }

    /**
     * Adds a marker to the location
     */
    private fun addMarker(point: Point) {

        if (selectedPoint == null) {
            annotationManager.deleteAll()
            val pointAnnotationOptions = CircleAnnotationOptions()
                .withPoint(point)
                .withCircleRadius(100.0)
                .withCircleOpacity(0.2)
                .withCircleColor("#000")
                .withCircleStrokeWidth(2.0)
                .withCircleStrokeColor("#ffffff")
            selectedPoint = annotationManager.create(pointAnnotationOptions)

            val randomPoint = generateRandomCoordinates(point.latitude(), point.longitude(),0.1)

            Glide.with(requireContext())
                .asBitmap()
                .load(R.drawable.ic_action_account)
                .circleCrop()
                .override(65, 65)
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {

                        val name = JsonPrimitive("John Doe")

                        val options = PointAnnotationOptions()
                            .withPoint(Point.fromLngLat(randomPoint.second, randomPoint.first))
                            .withIconImage(resource)
                            .withData(name)


                        val ano = pointAnnotationManager.create(options)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        TODO("Not yet implemented")
                    }
                })

            pointAnnotationManager.apply {
                addClickListener(
                    OnPointAnnotationClickListener {
                        Toast.makeText(requireContext(), "id: ${it.getData()}", Toast.LENGTH_LONG).show()
                        false
                    }
                )
            }

        } else {
            selectedPoint?.let {
                it.point = point
                annotationManager.update(it)
            }
        }
    }

    // Reacts to movement events on the map
    private val onMoveListener = object : OnMoveListener {
        // called at the beginning of a movement
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        // Reacts to motion events
        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        // called at the end of a movement
        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    /**
     * Called when it is necessary to untrack the camera's position relative to the user's position
     */
    private fun onCameraTrackingDismissed() {
        binding.mapView.apply {
            location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
            gestures.removeOnMoveListener(onMoveListener)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.apply {
            // cleaning listeners
            location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
            gestures.removeOnMoveListener(onMoveListener)
        }
    }

    fun generateRandomCoordinates(centerLat: Double, centerLng: Double, radius: Double): Pair<Double, Double> {
        val random = java.util.Random()

        // Generate a random angle within the circle
        val angle = random.nextDouble() * 2.0 * PI

        // Generate a random distance within the radius
        val distance = sqrt(random.nextDouble()) * radius

        // Calculate the new coordinates
        val newLat = centerLat + distance * cos(angle) / 111.32 // 1 degree of latitude is approximately 111.32 kilometers
        val newLng = centerLng + distance * sin(angle) / (111.32 * cos(centerLat * PI / 180)) // Correct for the longitude distance based on latitude

        return Pair(newLat, newLng)
    }

}