package com.example.weathersphere.ui.map

import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.weathersphere.ui.activity.MainActivity
import com.example.weathersphere.R
import com.example.weathersphere.databinding.FragmentMapBinding
import com.example.weathersphere.model.data.Place
import com.example.weathersphere.utils.Constants
import com.example.weathersphere.viewmodel.HomeViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding
    private var marker: Marker? = null
    private val args: MapFragmentArgs by navArgs()
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)

        setBottomNavVisibility(View.GONE)

        setupMap()

        setListeners()

        return binding.root
    }

    private fun setBottomNavVisibility(visibility: Int) {
        val mainActivity = requireActivity() as MainActivity
        mainActivity.binding.bottomNavigation.visibility = visibility
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun setListeners() {
        binding.btnSaveLocation.setOnClickListener {
            if (marker == null)
                Toast.makeText(requireContext(), "You Need To Select Your Location.", Toast.LENGTH_SHORT).show()
            else {
                val selectedLocation = getMarkerLocation()

                if(args.type == Constants.HOME) {
                    viewModel.setSelectedLocation(selectedLocation)
                } else if(args.type == Constants.FAVOURITE) {
                    viewModel.addPlaceToFavourite(
                        Place(
                            cityName = getCityName(selectedLocation),
                            latitude = selectedLocation.latitude,
                            longitude = selectedLocation.longitude
                        )
                    )
                }
                navigateBack()
            }
        }
    }

    // It Should Be With Navigation Component
    private fun navigateBack() {
        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction().commit()
        fragmentManager.popBackStack()
    }

    override fun onMapReady(map: GoogleMap) {
        map.setOnMapClickListener { latLng ->
            marker?.remove()

            marker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
            )

            marker?.title = getCityName(latLng)
        }
    }

    private fun getCityName(latLng: LatLng): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        var cityName = "UnKnown Location"
        if (address != null && address.size!= 0&& address[0].locality != null) {
            cityName = "${address[0].countryName} / ${address[0].locality}"
        } else if (address != null) {
            cityName = "${address[0].countryName} / ${address[0].adminArea}"
        }

        return cityName
    }

    private fun getMarkerLocation(): LatLng {
        return marker!!.position
    }

    override fun onDestroyView() {
        setBottomNavVisibility(View.VISIBLE)
        super.onDestroyView()
    }
}