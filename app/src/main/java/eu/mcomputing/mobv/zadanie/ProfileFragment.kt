package eu.mcomputing.mobv.zadanie

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.FileUtils
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.Companion.isPhotoPickerAvailable
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import eu.mcomputing.mobv.zadanie.broadcastReceivers.GeofenceBroadcastReceiver
import eu.mcomputing.mobv.zadanie.data.PreferenceData
import eu.mcomputing.mobv.zadanie.data.api.DataRepository
import eu.mcomputing.mobv.zadanie.databinding.FragmentProfileBinding
import eu.mcomputing.mobv.zadanie.viewmodels.AuthViewModel
import eu.mcomputing.mobv.zadanie.viewmodels.ProfileViewModel
import eu.mcomputing.mobv.zadanie.workers.MyWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class ProfileFragment : Fragment() {
    private var mapView: MapView? = null
    private lateinit var viewModel: ProfileViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var binding: FragmentProfileBinding

    private val PERMISSIONS_REQUIRED = when {
        Build.VERSION.SDK_INT >= 33 -> {// android 13m
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        else -> {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    /*val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            
        }*/


    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {

    }

    fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ProfileViewModel::class.java]

        authViewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[AuthViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*var mapView: MapView? = view.findViewById(R.id.mapView)
        mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)*/

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->

            var mapView: MapView? = bnd.mapView
            mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)

            /*val user = PreferenceData.getInstance().getUser(requireContext())
            if (user != null) {
                bnd.textEmail.text = user.email
            }*/

            bnd.changePasswdButton.setOnClickListener {
                // change password not implemented
                it.findNavController().navigate(R.id.action_to_changePassword)
            }

            bnd.timeStartButton.setOnClickListener {
                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    { _, hour, minute ->
                        viewModel.startHour.postValue(hour)
                        viewModel.startMinute.postValue(minute)
                        val selectedTime = "od %d:%d".format(hour, minute)
                        viewModel.startTime.postValue(selectedTime)
                    },
                    0, // Initial hour
                    0, // Initial minute
                    true // 24 hour mode
                )

                //cancel button
                timePickerDialog.setOnCancelListener {
                    viewModel.startHour.postValue(null)
                    viewModel.startMinute.postValue(null)
                    viewModel.startTime.postValue("od:")
                }

                timePickerDialog.show()
                /*val currentTime = LocalDateTime.now()
                Log.d("time", currentTime.hour.toString())*/
            }

            bnd.timeEndButton.setOnClickListener {
                var startHour = 0
                var startMinute = 0
                viewModel.startHour.value?.let {
                    startHour = it
                }

                viewModel.startMinute.value?.let {
                    startMinute = it
                }

                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    { _, hour, minute ->
                        viewModel.endHour.postValue(hour)
                        viewModel.endMinute.postValue(minute)
                        val selectedTime = "od %d:%d".format(hour, minute)
                        viewModel.endTime.postValue(selectedTime)
                    },
                    startHour, // Initial hour
                    startMinute, // Initial minute
                    true // 24 hour mode
                )

                //cancel button
                timePickerDialog.setOnCancelListener {
                    Log.d("button", "cancel")
                    viewModel.endHour.postValue(null)
                    viewModel.endMinute.postValue(null)
                    viewModel.endTime.postValue("do:")
                }

                timePickerDialog.show()
            }

            bnd.loadProfileBtn.setOnClickListener {
                val user = PreferenceData.getInstance().getUser(requireContext())
                user?.let {
                    viewModel.loadUser(it.id)
                }
            }

            viewModel.profileResult.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    Snackbar.make(
                        bnd.loadProfileBtn,
                        it,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            viewModel.userResult.observe(viewLifecycleOwner) {

                Picasso.get()
                    .load("https://upload.mcomputing.eu/" + viewModel.userResult.value?.photo )
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .placeholder(R.drawable.ic_action_account)
                    .into(bnd.imageView2)
            }

            // logout and clear user data
            bnd.logoutBtn.setOnClickListener {
                PreferenceData.getInstance().clearData(requireContext())
                authViewModel.clearUserResult()
                it.findNavController().navigate(R.id.action_profile_intro)
            }

            val scope = CoroutineScope(Dispatchers.Main)

            // Registers a photo picker activity launcher in single-select mode.
            val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                // Callback is invoked after the user selects a media item or closes the photo picker.
                if (uri != null) {
                    // persist media file access
                    val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    requireContext().contentResolver.takePersistableUriPermission(uri, flag)

                    Log.d("PhotoPicker", "Selected URI: $uri")
                    bnd.imageView2.setImageURI(uri)

                    val res = getRealPathFromURI(uri, requireContext())
                    scope.launch {
                        val response = res?.let {
                            DataRepository.getInstance(requireContext()).apiUploadProfilePicture(
                                it
                            )
                        }
                        if (response != null) {
                            Snackbar.make(
                                bnd.addImageBtn,
                                response,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }

                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

            // specific MIME type
            val mimeType = "image/jpeg"

            bnd.addImageBtn.setOnClickListener {
                // pick image and run code in picker
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.SingleMimeType(mimeType)))
            }

            bnd.deleteImageBtn.setOnClickListener {
                scope.launch {
                    val response = DataRepository.getInstance(requireContext()).apiDeleteProfilePicture()
                    if(response.second == true){
                        bnd.imageView2.setImageResource(R.drawable.ic_action_account)
                    }
                    Snackbar.make(
                        bnd.addImageBtn,
                        response.first,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            bnd.locationSwitch.isChecked = PreferenceData.getInstance().getSharing(requireContext())
            bnd.locationSwitch.setOnCheckedChangeListener { _, checked ->
                Log.d("ProfileFragment", "sharing je $checked")
                if (checked) {
                    turnOnSharing()
                } else {
                    turnOffSharing()
                }
            }

        }

    }

    //From: https://nobanhasan.medium.com/get-picked-image-actual-path-android-11-12-180d1fa12692
    private fun getRealPathFromURI(uri: Uri, context: Context): String? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        val nameIndex =  returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val file = File(context.filesDir, name)
        return file.path
    }

    /**
     * turns of location sharing, permission asked before if needed
     */
    @SuppressLint("MissingPermission")
    private fun turnOnSharing() {
        Log.d("ProfileFragment", "turnOnSharing")
        

        if (!hasPermissions(requireContext())) {
            Log.d("ProfileFragment", "no permissions")
            // no permission for location
            binding.locationSwitch.isChecked = false
            for (p in PERMISSIONS_REQUIRED) {
                requestPermissionLauncher.launch(p)
            }
            return
        }

        PreferenceData.getInstance().putSharing(requireContext(), true)

        // getting actual position
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) {
            // Logika pre prácu s poslednou polohou
            Log.d("ProfileFragment", "poloha posledna ${it ?: "-"}")
            if (it == null) {
                Log.e("ProfileFragment", "poloha neznama geofence nevytvoreny")
            } else {
                setupGeofence(it)
            }
        }

    }

    /**
     * turns of location sharing
     */
    private fun turnOffSharing() {
        Log.d("ProfileFragment", "turnOffSharing")
        PreferenceData.getInstance().putSharing(requireContext(), false)
        removeGeofence()
    }

    /**
     *  Setting up geofence with added location and radius
     */
    @SuppressLint("MissingPermission")
    private fun setupGeofence(location: Location) {

        val geofencingClient = LocationServices.getGeofencingClient(requireActivity())

        // creating geofence with position
        val geofence = Geofence.Builder()
            .setRequestId("my-geofence")
            .setCircularRegion(location.latitude, location.longitude, 100f) // 100m polomer
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        // creating geofence request
        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()


        // Geofence PendingIntent
        val intent = Intent(requireActivity(), GeofenceBroadcastReceiver::class.java)
        val geofencePendingIntent =
            PendingIntent.getBroadcast(
                requireActivity(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

        // adding Geofences to LocationServices.getGeofencingClient
        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                // Geofences boli úspešne pridané
                Log.d("ProfileFragment", "geofence vytvoreny")
                viewModel.updateGeofence(location.latitude, location.longitude, 100.0)

                runWorker()
            }
            addOnFailureListener {
                // Chyba pri pridaní geofences
                it.printStackTrace()
                binding.locationSwitch.isChecked = false
                PreferenceData.getInstance().putSharing(requireContext(), false)
            }
        }

    }

    /**
     *  Turn of existing Geofences
     */
    private fun removeGeofence() {
        Log.d("ProfileFragment", "geofence zruseny")
        val geofencingClient = LocationServices.getGeofencingClient(requireActivity())
        geofencingClient.removeGeofences(listOf("my-geofence"))

        viewModel.removeGeofence()
        cancelWorker()
    }

    private fun runWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        /*val repeatingRequest = PeriodicWorkRequestBuilder<MyWorker>(
            15, TimeUnit.MINUTES, // repeatInterval
            5, TimeUnit.MINUTES // flexInterval
        )
            .setConstraints(constraints)
            .addTag("myworker-tag")
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "myworker",
            ExistingPeriodicWorkPolicy.KEEP, // or REPLACE
            repeatingRequest
        )*/

        val data = Data.Builder()
        data.putString("file_path", "put_file_path_here")

        val myWorkRequest = OneTimeWorkRequestBuilder<MyWorker>().setInputData(data.build()).build()

        WorkManager.getInstance(requireContext()).enqueue(myWorkRequest)
    }

    private fun cancelWorker() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("myworker")
    }
}