package com.example.weathersphere.view.map

import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.weathersphere.R
import com.example.weathersphere.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding
    private lateinit var googleMap: GoogleMap
    private var marker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        setListeners()
        return binding.root
    }

    private fun setListeners() {
        binding.btnSaveLocation.setOnClickListener {
            if (marker == null)
                Toast.makeText(requireContext(), "You Need To Select Your Location.", Toast.LENGTH_SHORT).show()
            else {
                val selectedLocation = getSelectedLocation()

                val action = MapFragmentDirections.actionMapFragmentToHomeFragment()
                findNavController().navigate(action)
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.setOnMapClickListener { latLng ->
            marker?.remove()

            marker = googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
            )

            getCityName(latLng) { cityName ->
                marker?.title = cityName
            }
        }
    }

    private fun getCityName(latLng: LatLng, callback: (String) -> Unit) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        val cityName =
            if (addresses?.isNotEmpty() == true) addresses[0]?.locality ?: "Unknown" else "Unknown"
        callback(cityName)
    }

    private fun getSelectedLocation(): LatLng {
        return marker!!.position
    }
}