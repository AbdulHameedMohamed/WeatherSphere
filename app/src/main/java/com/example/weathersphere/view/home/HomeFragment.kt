package com.example.weathersphere.view.home

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

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var daysAdapter: DaysAdapter
    private lateinit var hoursAdapter: HoursAdapter

    companion object {
        private const val TAG = "HomeFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        setupViewModel()

        setupRecyclerViews()

        viewModel.getWeather(44.34, 10.99)

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
                viewModel.weatherFlow.collectLatest { status ->
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
}