package com.example.weathersphere.ui.setting

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.weathersphere.R
import com.example.weathersphere.databinding.FragmentSettingBinding
import com.example.weathersphere.model.datastore.WeatherDataStore
import com.example.weathersphere.model.repository.WeatherRepositoryImpl
import com.example.weathersphere.model.local.DatabaseProvider
import com.example.weathersphere.model.local.WeatherLocalDataSourceImpl
import com.example.weathersphere.model.remote.RetrofitClient
import com.example.weathersphere.model.remote.WeatherRemoteDataSourceImpl
import com.example.weathersphere.utils.Constants
import com.example.weathersphere.utils.changeLanguageLocaleTo
import com.example.weathersphere.utils.showToast
import com.example.weathersphere.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private lateinit var viewModel: HomeViewModel
    private val weatherDataStore by lazy {
        WeatherDataStore(requireContext())
    }

    private var locationAnimation: Animation? = null
    private var windAnimation: Animation? = null
    private var notificationAnimation: Animation? = null
    private var temperatureAnimation: Animation? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()

        cardsAnimation()

        setupRadioButtons()

        observeSettings()
    }

    private fun setupViewModel() {
        val weatherApi = WeatherRemoteDataSourceImpl(RetrofitClient.apiService)
        val productDao =
            WeatherLocalDataSourceImpl(DatabaseProvider.getDatabase(requireContext()).weatherDao)
        val repository = WeatherRepositoryImpl.getInstance(weatherApi, productDao)
        val viewModelFactory = HomeViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
    }

    private fun cardsAnimation() {
        initAnimations()

        showAnimations()
    }

    private fun initAnimations() {
        locationAnimation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.card_setting_translation)
        windAnimation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.card_setting_translation2)
        notificationAnimation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.card_setting_translation2)
        temperatureAnimation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.card_setting_translation3)
    }

    private fun showAnimations() {
        binding.cvLocation.startAnimation(windAnimation)
        binding.cvLanguage.startAnimation(notificationAnimation)
        binding.cvWind.startAnimation(locationAnimation)
        binding.cvNotification.startAnimation(locationAnimation)
        binding.cvTemp.startAnimation(temperatureAnimation)
    }

    private fun setupRadioButtons() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                updateLocationRadioButton(weatherDataStore.getLocation.first())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                updateWindSpeedRadioButton(weatherDataStore.getWindSpeed.first())
            }
        }

        binding.rgLanguage.clearCheck()
//        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                updateLanguageRadioButton(weatherDataStore.getLanguage.first())
//            }
//        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                updateNotificationRadioButton(weatherDataStore.isNotificationEnabled.first())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                updateTemperatureRadioButton(weatherDataStore.getTemperature.first())
            }
        }
    }

    private fun observeSettings() {
        binding.rgLocation.setOnCheckedChangeListener { _, checkedId ->
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    val locationValue =
                        if (checkedId == R.id.rb_map) Constants.MAP else Constants.GPS
                    weatherDataStore.setLocation(locationValue)
                }
            }
        }

        binding.radioGroupSettingWind.setOnCheckedChangeListener { _, checkedId ->
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    val windSpeedValue =
                        if (checkedId == R.id.rb_meter_second) Constants.METER_SEC else Constants.MILE_HOUR
                    weatherDataStore.setWindSpeed(windSpeedValue)
                }
            }
        }

        binding.rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    val languageValue =
                        if (checkedId == R.id.rb_arabic) Constants.ARABIC else Constants.ENGLISH
                    weatherDataStore.setLanguage(languageValue)
                    changeLanguageLocaleTo(languageValue)
                    requireContext().showToast("WeatherSphere is Restarting Now")
                    restartApplication()
                }
            }
        }

        binding.rgNotification.setOnCheckedChangeListener { _, checkedId ->
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    val notificationValue =
                        if (checkedId == R.id.rb_enable) Constants.ENABLE else Constants.DISABLE
                    weatherDataStore.setNotificationEnabled(notificationValue == Constants.ENABLE)
                }
            }
        }

        binding.rgTemperature.setOnCheckedChangeListener { _, checkedId ->
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    val temperatureValue = when (checkedId) {
                        R.id.rb_celsius -> Constants.CELSIUS
                        R.id.rb_kelvin -> Constants.KELVIN
                        else -> Constants.FAHRENHEIT
                    }
                    weatherDataStore.setTemperature(temperatureValue)
                }
            }
        }
    }

    private fun updateLocationRadioButton(location: String) {
        if (location == Constants.GPS) {
            binding.rgLocation.check(R.id.rb_gps)
        } else {
            binding.rgLocation.check(R.id.rb_map)
        }
    }

    private fun updateWindSpeedRadioButton(windSpeed: String) {
        if (windSpeed == Constants.MILE_HOUR) {
            binding.radioGroupSettingWind.check(R.id.rb_mile_hour)
        } else {
            binding.radioGroupSettingWind.check(R.id.rb_meter_second)
        }
    }

    private fun updateLanguageRadioButton(language: String) {
        if (language == Constants.ARABIC) {
            binding.rgLanguage.check(R.id.rb_arabic)
        } else {
            binding.rgLanguage.check(R.id.rb_english)
        }
    }

    private fun updateNotificationRadioButton(notificationEnabled: Boolean) {
        if (notificationEnabled) {
            binding.rgNotification.check(R.id.rb_enable)
        } else {
            binding.rgNotification.check(R.id.rb_disable)
        }
    }

    private fun updateTemperatureRadioButton(temperature: String) {
        when (temperature) {
            Constants.KELVIN -> binding.rgTemperature.check(R.id.rb_kelvin)
            Constants.FAHRENHEIT -> binding.rgTemperature.check(R.id.rb_fahrenheit)
            else -> binding.rgTemperature.check(R.id.rb_celsius)
        }
    }

    private fun restartApplication() {
        val intent = requireActivity().packageManager.getLaunchIntentForPackage(
            requireActivity().packageName
        )
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        requireActivity().finish()
        if (intent != null) {
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        destroyAnimations()
    }

    private fun destroyAnimations() {
        locationAnimation?.cancel()
        windAnimation?.cancel()
        notificationAnimation?.cancel()
        temperatureAnimation?.cancel()

        locationAnimation = null
        windAnimation = null
        notificationAnimation = null
        temperatureAnimation = null
    }
}