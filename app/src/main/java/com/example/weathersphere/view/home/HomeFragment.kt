package com.example.weathersphere.view.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.weathersphere.R
import com.example.weathersphere.databinding.DialogLocationBinding
import com.example.weathersphere.databinding.FragmentHomeBinding
import com.example.weathersphere.model.WeatherRepository
import com.example.weathersphere.model.WeatherResult
import com.example.weathersphere.model.local.DatabaseProvider
import com.example.weathersphere.model.local.WeatherLocalDataSource
import com.example.weathersphere.model.remote.RetrofitClient
import com.example.weathersphere.model.remote.WeatherRemoteDataSource
import com.example.weathersphere.viewmodel.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

class HomeFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var daysAdapter: DaysAdapter
    private lateinit var hoursAdapter: HoursAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
        private const val TAG = "HomeFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        setupRecyclerViews()

        setupViewModel()

        observeWeather()

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

    private fun setupRecyclerViews() {
        daysAdapter = DaysAdapter()
        hoursAdapter = HoursAdapter()
        binding.rvDays.adapter = daysAdapter
        binding.rvHours.adapter = hoursAdapter
    }

    private fun observeWeather() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weatherFlow
                    .collectLatest { status ->
                        when (status) {
                            is WeatherResult.Loading -> {

                            }

                            is WeatherResult.Success -> {
                                daysAdapter.submitList(status.data.forecasts)
                                hoursAdapter.submitList(status.data.forecasts)

                                Log.d(TAG, "onCreateView: Success ${status.data}")
                            }

                            is WeatherResult.Error -> {
                                Log.e(TAG, "onCreateView: Error ${status.exception}")
                            }
                        }
                    }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        showChooseLocationDialog()
    }

    private fun requestLocationPermission() {
        if (EasyPermissions.hasPermissions(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            fetchLocationAndWeather()
        } else {
            EasyPermissions.requestPermissions(
                PermissionRequest.Builder(this, LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION)
                    .setRationale(getString(R.string.location_permission_rationale))
                    .build()
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocationAndWeather() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val latitude = it.latitude
                    val longitude = it.longitude
                    viewModel.getWeather(latitude, longitude)
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            fetchLocationAndWeather()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun showChooseLocationDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding: DialogLocationBinding = DialogLocationBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.btnSave.setOnClickListener {
            val selectedOptionId = dialogBinding.rgLocation.checkedRadioButtonId
            when (selectedOptionId) {
                R.id.radio_gps -> {
                    requestLocationPermission()
                }
                R.id.radio_map -> {
                    navigationToMapFragment()
                }
            }
            dialog.dismiss()
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun navigationToMapFragment() {

    }
}