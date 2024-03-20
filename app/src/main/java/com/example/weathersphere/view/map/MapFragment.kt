package com.example.weathersphere.view.map

import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.weathersphere.R
import com.example.weathersphere.databinding.FragmentMapBinding
import com.example.weathersphere.model.WeatherRepository
import com.example.weathersphere.model.data.Place
import com.example.weathersphere.model.local.DatabaseProvider
import com.example.weathersphere.model.local.WeatherLocalDataSource
import com.example.weathersphere.model.remote.RetrofitClient
import com.example.weathersphere.model.remote.WeatherRemoteDataSource
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
    private lateinit var viewModel: HomeViewModel
    val args: MapFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)

        setupViewModel()

        setupMap()

        setListeners()
        return binding.root
    }

    private fun setupViewModel() {
        val productsApi = WeatherRemoteDataSource(RetrofitClient.apiService)
        val productDao =
            WeatherLocalDataSource(DatabaseProvider.getDatabase(requireContext()).weatherDao)
        val repository = WeatherRepository.getInstance(productsApi, productDao)
        val viewModelFactory = HomeViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
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
                val selectedLocation = getSelectedLocation()

                if(args.type == Constants.HOME) {
                    viewModel.getWeather(selectedLocation)
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
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        return if (addresses?.isNotEmpty() == true) addresses[0]?.locality
            ?: "Unknown" else "Unknown"
    }

    private fun getSelectedLocation(): LatLng {
        return marker!!.position
    }
}