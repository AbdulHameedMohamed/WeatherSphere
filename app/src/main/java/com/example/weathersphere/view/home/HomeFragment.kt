package com.example.weathersphere.view.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.weathersphere.R
import com.example.weathersphere.databinding.DialogLocationBinding
import com.example.weathersphere.databinding.FragmentHomeBinding
import com.example.weathersphere.model.LocationDataStore
import com.example.weathersphere.model.WeatherRepository
import com.example.weathersphere.model.WeatherResult
import com.example.weathersphere.model.local.DatabaseProvider
import com.example.weathersphere.model.local.WeatherLocalDataSource
import com.example.weathersphere.model.remote.RetrofitClient
import com.example.weathersphere.model.remote.WeatherRemoteDataSource
import com.example.weathersphere.utils.Constants
import com.example.weathersphere.utils.changeLanguageLocaleTo
import com.example.weathersphere.utils.getLanguageLocale
import com.example.weathersphere.viewmodel.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import java.util.Locale

class HomeFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationDataStore by lazy {
        LocationDataStore(requireContext())
    }
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter

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

        val latLng = LatLng(36.778259, -119.417931)
        viewModel.getWeather(latLng)
        observeWeather()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (getLanguageLocale().isBlank()) {
            changeLanguageLocaleTo(Locale.getDefault().language)
        } else {
            val locationSelected = locationDataStore.locationSelected
            lifecycleScope.launch {
                locationSelected.collect { selected ->
                    if (!selected) {
                        showInitialSettingDialog()
                    } else {
                        // Location is selected
                    }
                }
            }
        }
    }

    private fun setupViewModel() {
        val weatherApi = WeatherRemoteDataSource(RetrofitClient.apiService)
        val productDao =
            WeatherLocalDataSource(DatabaseProvider.getDatabase(requireContext()).weatherDao)
        val repository = WeatherRepository.getInstance(weatherApi, productDao)
        val viewModelFactory = HomeViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
    }

    private fun setupRecyclerViews() {
        dailyAdapter = DailyAdapter()
        binding.rvDays.adapter = dailyAdapter

        hourlyAdapter = HourlyAdapter()
        binding.rvHours.adapter = hourlyAdapter
    }

    private fun observeWeather() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weatherFlow
                    .collectLatest { status ->
                        when (status) {
                            is WeatherResult.Loading -> {
                                Log.d(TAG, "observeWeather: Loading")
                            }

                            is WeatherResult.Success -> {
                                Log.d(TAG, "onCreateView: Success")
                            }

                            is WeatherResult.Error -> {
                                Log.e(TAG, "onCreateView: Error ${status.exception}")
                            }
                        }
                    }
            }

            viewModel.selectedLocation.collectLatest { latLng ->
                latLng?.let {
                    viewModel.getWeather(latLng)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private fun requestLocationPermission() {
        if (EasyPermissions.hasPermissions(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            fetchLocationAndWeather()
        } else {
            EasyPermissions.requestPermissions(
                PermissionRequest.Builder(
                    this,
                    LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
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
                    val latLng = LatLng(it.latitude, it.longitude)
                    viewModel.getWeather(latLng)
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
            setLocation()
            fetchLocationAndWeather()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun showInitialSettingDialog() {
        val bindingInitialLayoutDialog = DialogLocationBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(bindingInitialLayoutDialog.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = layoutParams

        bindingInitialLayoutDialog.btnSave.setOnClickListener {
            val checkedBtn = bindingInitialLayoutDialog.rgLocation.checkedRadioButtonId

            if (checkedBtn == bindingInitialLayoutDialog.radioGps.id) {
                requestLocationPermission()
            } else {
                navigationToMapFragment()
            }

            dialog.dismiss()
        }

        bindingInitialLayoutDialog.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun navigationToMapFragment() {
        setLocation()
        val action = HomeFragmentDirections.actionHomeFragmentToMapFragment(Constants.HOME)
        findNavController().navigate(action)
    }

    private fun setLocation() {
        lifecycleScope.launch {
            locationDataStore.setLocationSelected(true)
        }
    }
}