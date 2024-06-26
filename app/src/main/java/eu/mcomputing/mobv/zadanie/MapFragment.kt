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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonPrimitive
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.style
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
import eu.mcomputing.mobv.zadanie.data.api.DataRepository
import eu.mcomputing.mobv.zadanie.databinding.FragmentMapBinding
import eu.mcomputing.mobv.zadanie.viewmodels.FeedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Math.PI
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt
import java.lang.Thread.sleep
import java.time.LocalTime


class MapFragment : Fragment() {
    private lateinit var binding: FragmentMapBinding
    private var selectedPoint: CircleAnnotation? = null
    private var lastLocation: Point? = null
    private lateinit var annotationManager: CircleAnnotationManager
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private lateinit var feedViewModel: FeedViewModel

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializácia feedViewModel
        feedViewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[FeedViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->

            val sharing = PreferenceData.getInstance().getSharing(requireContext())

            val startTime = PreferenceData.getInstance().getStartSharingTime(context)
            val endTime = PreferenceData.getInstance().getEndSharingTime(context)

            var isBetween = true

            // start and end times are set
            if (startTime != null && endTime != null) {
                // calculate interval
                val currentTime = LocalTime.now()
                isBetween = currentTime.isAfter(startTime) && currentTime.isBefore(endTime)
            }

            // check if sharing is on
            if (sharing && isBetween) {

                annotationManager = bnd.mapView.annotations.createCircleAnnotationManager()
                pointAnnotationManager = bnd.mapView.annotations.createPointAnnotationManager()

                // get logged user
                val userMe = PreferenceData.getInstance().getUser(requireContext())

                var center = Point.fromLngLat(0.0, 0.0)

                val scope = CoroutineScope(Dispatchers.Main)
                scope.launch {
                    // get all users
                    val users =
                        DataRepository.getInstance(requireContext()).getUsersList() ?: emptyList()

                    // logged user radius
                    var radius = 100.0

                    // find logged user to get possition
                    for (u in users) {
                        if (u.uid == userMe?.id) {
                            center = Point.fromLngLat(u.lon, u.lat)
                            radius = u.radius
                            Log.d("mapfragment", u.toString())
                        }
                    }

                    // draw circle
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
                    bnd.mapView.getMapboxMap()
                        .setCamera(CameraOptions.Builder().center(center).zoom(15.0).build())

                    // draw user photos to the circle
                    for (u in users) {
                        if (u.uid != userMe?.id) {
                            val photo =
                                if (u.photo == "") R.drawable.ic_action_account else "https://upload.mcomputing.eu/" + u.photo

                            // generate random point in the circle
                            val randomPoint = generateRandomCoordinates(
                                center.latitude(),
                                center.longitude(),
                                radius / 1000.0
                            )
                            val userData = JsonPrimitive(u.uid)

                            Glide.with(requireContext())
                                .asBitmap()
                                .load(photo)
                                .circleCrop()
                                .override(65, 65)
                                .into(object : CustomTarget<Bitmap>() {
                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: Transition<in Bitmap>?
                                    ) {
                                        val options = PointAnnotationOptions()
                                            .withPoint(
                                                Point.fromLngLat(
                                                    randomPoint.second,
                                                    randomPoint.first
                                                )
                                            )
                                            .withIconImage(resource)
                                            .withData(userData)


                                        pointAnnotationManager.create(options)
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {
                                        // implementation
                                    }
                                })

                            bnd.myLocation.setOnClickListener {
                                bnd.mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(center).zoom(15.0).build())
                            }

                            pointAnnotationManager.apply {
                                addClickListener(
                                    OnPointAnnotationClickListener {
                                        for (user in users) {
                                            if (user.uid == it.getData().toString().replace("\"", "")) {
                                                Log.d("mapfragment", user.name)
                                                feedViewModel.selectedUser.postValue(user)
                                                //findNavController(view).navigate(R.id.action_to_other_profile)
                                                break
                                            }
                                        }
                                        findNavController(view).navigate(R.id.action_to_other_profile)

                                        true
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                val snackbar = Snackbar
                    .make(view, "Turn on sharing to show users", Snackbar.LENGTH_LONG)
                    .setAction("Turn on") {
                        findNavController(view).navigate(R.id.action_to_profile)
                    }
                    .show()
            }

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