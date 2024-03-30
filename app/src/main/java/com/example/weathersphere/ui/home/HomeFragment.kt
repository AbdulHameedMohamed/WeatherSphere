package com.example.weathersphere.ui.home

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
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.weathersphere.R
import com.example.weathersphere.databinding.DialogLocationBinding
import com.example.weathersphere.databinding.FragmentHomeBinding
import com.example.weathersphere.model.datastore.WeatherDataStore
import com.example.weathersphere.model.WeatherResult
import com.example.weathersphere.model.data.Weather
import com.example.weathersphere.model.data.WeatherResponse
import com.example.weathersphere.utils.Constants
import com.example.weathersphere.utils.changeLanguageLocaleTo
import com.example.weathersphere.utils.fromUnixToString
import com.example.weathersphere.utils.fromUnixToStringTime
import com.example.weathersphere.utils.getLanguageLocale
import com.example.weathersphere.utils.setIcon
import com.example.weathersphere.utils.setLocationNameByGeoCoder
import com.example.weathersphere.viewmodel.HomeViewModel
import com.github.matteobattilana.weather.PrecipType
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
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val weatherDataStore by lazy {
        WeatherDataStore(requireContext())
    }
    private val viewModel: HomeViewModel by activityViewModels()

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
        private const val TAG = "HomeFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        animateWeather()

        setupRecyclerViews()

        observeWeather()

        setListeners()
        return binding.root
    }

    private fun setListeners() {
        binding.btnMyLocation.setOnClickListener {
            fetchLocationAndWeather()
        }
    }

    private fun animateWeather() {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.place_changer)
        binding.ivWeather.startAnimation(animation)
    }

    private fun setupRecyclerViews() {
        dailyAdapter = DailyAdapter(WeatherDataStore(requireContext()), lifecycleScope)
        binding.rvDays.adapter = dailyAdapter

        hourlyAdapter = HourlyAdapter(WeatherDataStore(requireContext()), lifecycleScope)
        binding.rvHours.adapter = hourlyAdapter
    }

    private fun observeWeather() {
        observeSelectedLocation()
        observeWeatherFlow()
    }

    private fun observeWeatherFlow() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weatherFlow.collectLatest { status ->
                    when (status) {
                        is WeatherResult.Loading -> {
                            Log.d(TAG, "observeWeather: Loading")
                            binding.svHome.visibility = View.GONE
                            binding.lvLoading.visibility = View.VISIBLE
                        }

                        is WeatherResult.Success -> {
                            if (status.data != null) {
                                binding.svHome.visibility = View.VISIBLE
                                binding.lvLoading.visibility = View.GONE
                                setWeatherAnimation(status.data.current.weather)
                                bindWeatherData(status.data)
                            } else showInitialSettingDialog()
                        }

                        is WeatherResult.Error -> {
                            binding.lvLoading.visibility = View.GONE
                            binding.lvError.visibility = View.VISIBLE
                            Log.e(TAG, "onCreateView: Error ${status.exception}")
                            showInitialSettingDialog()
                        }
                    }
                }
            }
        }
    }

    private fun observeSelectedLocation() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedLocation.collect { location ->
                    if (location != null) {
                        weatherDataStore.getLanguage.collect { language ->
                            if (language == Constants.ARABIC)
                                viewModel.getWeather(location, "ar")
                            else
                                viewModel.getWeather(location, "en")
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (getLanguageLocale().isBlank()) {
            changeLanguageLocaleTo(Locale.getDefault().language)
        }
    }

    private fun bindWeatherData(weatherResponse: WeatherResponse) {
        setIcon(weatherResponse.current.weather[0].icon, binding.ivWeather)

        binding.apply {

            tvLocationName.text =
                setLocationNameByGeoCoder(weatherResponse, requireContext())
            tvWeatherStatus.text = weatherResponse.current.weather[0].description
            tvDynamicPressure.text =
                String.format(
                    "%d %s",
                    weatherResponse.current.pressure,
                    getString(R.string.hpa)
                )
            tvDynamicHumidity.text = String.format(
                "%d %s",
                weatherResponse.current.humidity,
                getString(R.string.percentage)
            )

            tvDynamicCloud.text = String.format(
                "%d %s",
                weatherResponse.current.clouds,
                getString(R.string.percentage)
            )
            tvDynamicViolet.text = String.format("%.1f", weatherResponse.current.uvi)
            tvDynamicVisibility.text =
                String.format(
                    "%d %s",
                    weatherResponse.current.visibility,
                    getString(R.string.m)
                )

            lifecycleScope.launch {
                weatherDataStore.getLanguage.collect { language ->
                    if (language == Constants.ARABIC) {
                        tvDate.text = fromUnixToString(weatherResponse.current.dt, "ar")
                        tvSunRise.text =
                            fromUnixToStringTime(weatherResponse.current.sunrise, "ar")
                        tvSunSet.text =
                            fromUnixToStringTime(weatherResponse.current.sunset, "ar")
                    } else {
                        tvDate.text = fromUnixToString(weatherResponse.current.dt, "en")
                        tvSunRise.text =
                            fromUnixToStringTime(weatherResponse.current.sunrise, "en")
                        tvSunSet.text =
                            fromUnixToStringTime(weatherResponse.current.sunset, "en")
                    }
                }
            }

            lifecycleScope.launch {
                weatherDataStore.getTemperature.collect { temperature ->
                    when (temperature) {
                        Constants.KELVIN -> tvCurrentDegree.text = String.format(
                            "%.1f°${getString(R.string.k)}",
                            weatherResponse.current.temp + 273.15
                        )

                        Constants.FAHRENHEIT -> tvCurrentDegree.text = String.format(
                            "%.1f°${getString(R.string.f)}",
                            weatherResponse.current.temp * 9 / 5 + 32
                        )

                        else -> tvCurrentDegree.text = String.format(
                            "%.1f°${getString(R.string.c)}",
                            weatherResponse.current.temp
                        )
                    }
                }
            }

            lifecycleScope.launch {
                weatherDataStore.getWindSpeed.collect { windSpeed ->
                    when (windSpeed) {
                        Constants.MILE_HOUR -> tvDynamicWind.text = String.format(
                            "%.1f ${getString(R.string.mile_hour)}",
                            weatherResponse.current.wind_speed * 2.237
                        )

                        else -> tvDynamicWind.text = String.format(
                            "%.1f ${getString(R.string.meter_second)}",
                            weatherResponse.current.wind_speed
                        )
                    }
                }
            }

            hourlyAdapter.submitList(weatherResponse.hourly)
            dailyAdapter.submitList(weatherResponse.daily.filterIndexed { index, _ -> index != 0 }
                .sortedWith(compareBy { it.dt }))
        }
    }

    private fun setWeatherAnimation(weatherConditions: List<Weather>) {

        Log.d(TAG, "setWeatherAnimation: $weatherConditions")
        Log.d(TAG, "setWeatherAnimation: $${weatherConditions[0].main}")
        Log.d(TAG, "setWeatherAnimation: $${weatherConditions[0].description}")
        val isRainy = weatherConditions.any { it.main.lowercase() == "rain" }
        val isSnowy = weatherConditions.any { it.main.lowercase() == "snow" }

        if (isRainy) {
            binding.wvHome.setWeatherData(PrecipType.RAIN)
        } else if (isSnowy) {
            binding.wvHome.setWeatherData(PrecipType.SNOW)
        } else {
            binding.wvHome.setWeatherData(PrecipType.CLEAR)
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
                Log.d(TAG, "fetchLocationAndWeather: $location")
                location?.let {
                    viewModel.setSelectedLocation(LatLng(it.latitude, it.longitude))
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
            Toast.makeText(
                requireContext(),
                "Please select your location on the map Instead.",
                Toast.LENGTH_SHORT
            )
                .show()
            navigationToMapFragment()
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
                viewLifecycleOwner.lifecycleScope.launch {
                    weatherDataStore.setLocation(Constants.GPS)
                }
                requestLocationPermission()
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    weatherDataStore.setLocation(Constants.MAP)
                }
                navigationToMapFragment()
            }

            dialog.dismiss()
        }
        dialog.show()
    }

    private fun navigationToMapFragment() {
        val action = HomeFragmentDirections.actionHomeFragmentToMapFragment(Constants.HOME)
        findNavController().navigate(action)
    }
}