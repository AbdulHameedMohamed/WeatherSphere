package com.example.weathersphere.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.weathersphere.R
import com.example.weathersphere.databinding.FragmentHomeBinding
import com.example.weathersphere.model.WeatherRepository
import com.example.weathersphere.model.WeatherResult
import com.example.weathersphere.model.local.DatabaseProvider
import com.example.weathersphere.model.local.WeatherLocalDataSource
import com.example.weathersphere.model.remote.RetrofitClient
import com.example.weathersphere.model.remote.WeatherRemoteDataSource
import com.example.weathersphere.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    companion object {
        private const val TAG = "HomeFragment"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        val productsApi = WeatherRemoteDataSource(RetrofitClient.apiService)
        val productDao = WeatherLocalDataSource(DatabaseProvider.getDatabase(requireContext()).weatherDao)

        val repository = WeatherRepository.getInstance(productsApi, productDao)
        val viewModelFactory = HomeViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        viewModel.getWeather(44.34, 10.99)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.weather.collectLatest { status ->
                    when (status) {
                        is WeatherResult.Loading -> {
                            Log.d(TAG, "onCreateView: Loading")
                        }

                        is WeatherResult.Success -> {
                            Log.d(TAG, "onCreateView: Success" + status.data)
                        }

                        is WeatherResult.Error -> {
                            Log.e(TAG, "onCreateView: Error ${status.exception}")
                        }
                    }
                }
            }
        }

        return binding.root
    }
}