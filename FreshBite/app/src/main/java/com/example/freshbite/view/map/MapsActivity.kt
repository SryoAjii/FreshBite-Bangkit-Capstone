package com.example.freshbite.view.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.freshbite.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.example.freshbite.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        }
        placesClient = Places.createClient(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(this))
        getMyLocation()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val fineLocation = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocation = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            when {
                fineLocation -> {
                    getMyLocation()
                    toast("Menggunakan Lokasi Akurat")
                }
                coarseLocation -> {
                    getMyLocation()
                    toast("Menggunakan Perkiraan Lokasi")
                }
                else -> {
                    toast("Permission Ditolak")
                    showDefaultLocation()
                    findNearestFruitMarkets(-6.200000, 106.816666)
                }
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val lat = location.latitude
                        val lng = location.longitude
                        val userLocation = LatLng(lat, lng)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                        findNearestFruitMarkets(lat, lng)
                    } else {
                        toast("Unable to determine current location")
                    }
                }
                .addOnFailureListener {
                    toast("Error getting current location: ${it.message}")
                    Log.e("Error getting location:", "${it.message}")
                }
        } else {
            requestPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    private fun findNearestFruitMarkets(lat: Double, lng: Double) {
        Log.d("MapsActivity", "lat $lat, $lng")
        val apiKey = getString(R.string.google_maps_key)
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$lat,$lng&radius=1500&type=store&keyword=buah&key=$apiKey"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                runOnUiThread {
                    toast("Error finding places: ${e.message}")
                    Log.e("Error finding places:", "${e.message}")
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.string()?.let {
                    Log.d("API Response", it)
                    val jsonObject = JSONObject(it)
                    val results = jsonObject.getJSONArray("results")
                    if (results.length() == 0) {
                        runOnUiThread {
                            toast("Tidak ada toko buah di sekitar")
                        }
                    } else {
                        for (i in 0 until results.length()) {
                            val place = results.getJSONObject(i)
                            val name = place.getString("name")
                            val location = place.getJSONObject("geometry").getJSONObject("location")
                            val lat = location.getDouble("lat")
                            val lng = location.getDouble("lng")
                            val latLng = LatLng(lat, lng)

                            val openNow = if (place.has("opening_hours")) {
                                val isOpen = place.getJSONObject("opening_hours").getBoolean("open_now")
                                if (isOpen) "Open now" else "Closed now"
                            } else {
                                "Open status unavailable"
                            }

                            val address = place.optString("vicinity", "Address unavailable")

                            val photos = place.optJSONArray("photos")
                            val photoReference = photos?.getJSONObject(0)?.getString("photo_reference")
                            val photoUrl = if (photoReference != null) {
                                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$photoReference&key=$apiKey"
                            } else {
                                ""
                            }

                            val snippet = "$openNow|$address|$photoUrl"

                            runOnUiThread {
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(latLng)
                                        .title(name)
                                        .snippet(snippet)
                                )
                                if (i == 0) {
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    private fun showDefaultLocation() {
        val defaultLocation = LatLng(-6.200000, 106.816666) // Jakarta, Indonesia
        mMap.addMarker(MarkerOptions().position(defaultLocation).title("Jakarta"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
