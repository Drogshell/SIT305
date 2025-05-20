package com.trevin.lostandfound.ui.activities

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.trevin.lostandfound.BuildConfig
import com.trevin.lostandfound.R
import com.trevin.lostandfound.data.model.AdvertItem
import com.trevin.lostandfound.data.model.AdvertItemViewModel
import com.trevin.lostandfound.databinding.ActivityNewAdvertBinding
import com.trevin.lostandfound.ui.PlacePredictionAdapter
import com.trevin.lostandfound.util.LocationHelper
import com.trevin.lostandfound.util.MapHelper
import com.trevin.lostandfound.util.Validation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewAdvertActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewAdvertBinding
    private val viewModel: AdvertItemViewModel by viewModels()

    private var selectedLongitude: Double? = null
    private var selectedLatitude: Double? = null

    private lateinit var placesClient: PlacesClient
    private lateinit var fusedClient: FusedLocationProviderClient
    private var selectedDate: Date? = null
    private var isProgrammaticLocationChange = false

    private var googleMap: GoogleMap? = null
    private var mapMarker: Marker? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        super.onCreate(savedInstanceState)

        binding = ActivityNewAdvertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = buildTransition()
        window.sharedElementExitTransition = buildTransition()
        window.reenterTransition = buildTransition()


        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.new_advert_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up Places and Location
        Places.initializeWithNewPlacesApiEnabled(this, BuildConfig.apiKey)
        placesClient = Places.createClient(this)
        fusedClient = LocationServices.getFusedLocationProviderClient(this)

        // Set up Google Maps
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            googleMap = map
            MapHelper.setUpMapUI(map, false)
        }

        // Set up address autocomplete
        val locationField = binding.editTextLocation
        val suggestions = mutableListOf<AutocompletePrediction>()
        val adapter = PlacePredictionAdapter(this, suggestions)

        locationField.threshold = 1
        locationField.setAdapter(adapter)

        // All the listeners
        binding.editTextTitle.addTextChangedListener { validTitle() }
        binding.editTextDescription.addTextChangedListener { validDescription() }
        binding.editTextDate.addTextChangedListener { validDate() }
        binding.editTextPhone.addTextChangedListener { editable ->
            val phoneText = editable.toString()
            if (!Validation.isValidPhoneNumber(phoneText)) {
                binding.editTextPhoneLayout.error = "Invalid Number"
            } else {
                binding.editTextPhoneLayout.error = null
            }
        }

        binding.editTextDate.setOnClickListener { showDatePicker() }

        // Fetch predictions as user types
        locationField.addTextChangedListener { text ->
            if (isProgrammaticLocationChange) return@addTextChangedListener
            if (text.isNullOrBlank()) {
                binding.mapContainer.visibility = View.GONE
            }
            val question = text.toString()
            if (question.length >= 3) fetchPredictions(question, suggestions, adapter)
        }

        // When user clicks a suggestion, fetch and display details and update map
        locationField.setOnItemClickListener { parent, _, pos, _ ->
            val prediction = suggestions[pos]
            fetchPlaceDetails(prediction.placeId)
        }

        binding.buttonGetCurrentLocation.setOnClickListener {
            if (!LocationHelper.requestLocationPermission(this)) return@setOnClickListener
            LocationHelper.getCurrentLocation(
                this,
                fusedClient,
                onSuccess = ::onLatLngFound,
                onFailure = ::handleLocationError
            )
        }

        binding.buttonSave.setOnClickListener {
            if (validTitle() && validDescription() && validDate()) {
                saveAdvert()
            }
        }
    }

    private fun handleLocationError(error: String) {
        runOnUiThread { Toast.makeText(this, error, Toast.LENGTH_LONG).show() }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun onLatLngFound(lat: Double, lng: Double) {
        LocationHelper.reverseGeocode(this, lat, lng, onSuccess = { addr ->
            runOnUiThread {
                isProgrammaticLocationChange = true
                binding.editTextLocation.setText(addr)
                isProgrammaticLocationChange = false
                selectedLatitude = lat
                selectedLongitude = lng
                updateMap(lat, lng)
            }
        }, onFailure = ::handleLocationError)
    }

    private fun buildTransition(): MaterialContainerTransform {
        return MaterialContainerTransform().apply {
            addTarget(R.id.new_advert_root)
            duration = 400
            pathMotion = MaterialArcMotion()
            interpolator = FastOutSlowInInterpolator()
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
        }
    }

    // Get address suggestions from Places
    private fun fetchPredictions(
        query: String,
        predictions: MutableList<AutocompletePrediction>,
        adapter: PlacePredictionAdapter,
    ) {
        val request = FindAutocompletePredictionsRequest
            .builder()
            .setQuery(query)
            .setCountries(listOf("AU"))
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { resp ->
                predictions.clear()
                predictions.addAll(resp.autocompletePredictions)
                adapter.currentQuery = binding.editTextLocation.text?.toString() ?: ""
                adapter.sortByBestMatch()
                adapter.notifyDataSetChanged()
                if (predictions.isNotEmpty()) binding.editTextLocation.showDropDown()
            }
            .addOnFailureListener { e ->
                Log.e("PLACES_AUTOCOMPLETE", "Prediction failure: ${e.localizedMessage}", e)
                Toast.makeText(this, "Autocomplete error: ${e.localizedMessage}", Toast.LENGTH_LONG)
                    .show()
            }
    }

    // When a suggestion is chosen, fetch full place details and update map
    private fun fetchPlaceDetails(placeId: String) {
        val fields = listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG)
        val req = FetchPlaceRequest.builder(placeId, fields).build()

        placesClient.fetchPlace(req)
            .addOnSuccessListener { resp ->
                binding.mapContainer.visibility = View.VISIBLE
                val place = resp.place
                isProgrammaticLocationChange = true
                binding.editTextLocation.setText(place.address)
                binding.editTextLocation.dismissDropDown()
                isProgrammaticLocationChange = false
                selectedLatitude = place.latLng?.latitude
                selectedLongitude = place.latLng?.longitude
                if (selectedLatitude != null && selectedLongitude != null) {
                    updateMap(selectedLatitude!!, selectedLongitude!!)
                }
            }
            .addOnFailureListener { it.printStackTrace() }
    }

    // Update Google Map with new coordinates
    private fun updateMap(lat: Double, lng: Double) {
        if (googleMap == null) return
        if (mapMarker == null) {
            mapMarker = MapHelper.addMarker(googleMap!!, lat, lng)
        } else {
            mapMarker?.position = LatLng(lat, lng)
        }
        binding.mapContainer.visibility = View.VISIBLE
        MapHelper.moveCamToLocation(googleMap!!, lat, lng)
    }

    private fun saveAdvert() {
        val rawPhoneNum = binding.editTextPhone.text.toString().trim()
        val formattedNum =
            if (rawPhoneNum.isNotEmpty() && Validation.isValidPhoneNumber(rawPhoneNum)) {
                Validation.formatPhoneNumber(rawPhoneNum)
            } else {
                ""
            }

        val advertToCreate = AdvertItem(
            postType = if (binding.radioButtonLost.isChecked) "Lost" else "Found",
            title = binding.editTextTitle.text.toString().trim(),
            phoneNumber = formattedNum,
            description = binding.editTextDescription.text.toString().trim(),
            date = selectedDate!!,
            location = binding.editTextLocation.text.toString(),
            latitude = selectedLatitude ?: -1.0,
            longitude = selectedLongitude ?: -1.0
        )
        viewModel.createAdvert(advertToCreate)
        finish()
    }

    private fun validTitle(): Boolean {
        var valid = true
        val title = binding.editTextTitle.text.toString().trim()
        if (title.isEmpty()) {
            binding.editTextTitleLayout.error = "You must enter a title!"
            valid = false
        } else {
            binding.editTextTitleLayout.error = null
        }
        binding.buttonSave.isEnabled = valid
        return valid
    }

    private fun validDescription(): Boolean {
        var valid = true
        val description = binding.editTextDescription.text.toString().trim()
        if (description.isEmpty()) {
            binding.editTextDescriptionLayout.error = "Tell people about what was lost"
            valid = false
        } else if (description.length > 400) {
            binding.editTextDescriptionLayout.error = "Too many words!"
            valid = false
        } else {
            binding.editTextDescriptionLayout.error = null
        }
        binding.buttonSave.isEnabled = valid
        return valid
    }

    private fun validDate(): Boolean {
        var valid = true
        val date = binding.editTextDate.text.toString().trim()
        if (date.isEmpty()) {
            binding.editTextDateLayout.error = "When did you lose it?"
            valid = false
        } else {
            binding.editTextDateLayout.error = null
        }
        binding.buttonSave.isEnabled = valid
        return valid
    }

    private fun showDatePicker() {
        val today = MaterialDatePicker.todayInUtcMilliseconds()

        val constraintsBuilder = CalendarConstraints.Builder()
            .setEnd(today)
            .setValidator(DateValidatorPointBackward.now())

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Pick a date")
            .setSelection(today)
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        datePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            selectedDate = Date(selection)
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.editTextDate.setText(sdf.format(selectedDate))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LocationHelper.LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            binding.buttonGetCurrentLocation.performClick()
        }
    }
}
