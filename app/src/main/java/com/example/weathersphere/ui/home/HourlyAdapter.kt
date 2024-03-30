package com.example.weathersphere.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weathersphere.R
import com.example.weathersphere.databinding.ItemHoursBinding
import com.example.weathersphere.model.data.Hourly
import com.example.weathersphere.model.datastore.WeatherDataStore
import com.example.weathersphere.utils.Constants
import com.example.weathersphere.utils.formatDateStamp
import com.example.weathersphere.utils.formatTimestamp
import com.example.weathersphere.utils.setIconFromApi
import kotlinx.coroutines.launch

class HourlyAdapter(
    private val weatherDataStore: WeatherDataStore,
    private val lifecycleScope: LifecycleCoroutineScope
) : ListAdapter<Hourly, HourlyAdapter.ViewHolder>(DiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.createHourlyViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hourly = getItem(position)
        holder.onBind(hourly, weatherDataStore, lifecycleScope)
    }

    class ViewHolder(private val binding: ItemHoursBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun createHourlyViewHolder(parent: ViewGroup): ViewHolder {
                val binding =
                    ItemHoursBinding.inflate(LayoutInflater.from(parent.context), parent, false)

                return ViewHolder(binding)
            }
        }

        fun onBind(
            hourly: Hourly,
            weatherDataStore: WeatherDataStore,
            lifecycleScope: LifecycleCoroutineScope
        ) {
            binding.apply {
                lifecycleScope.launch {
                    weatherDataStore.getLanguage.collect { language ->
                        if (language == Constants.ARABIC) {
                            tvHour.text = formatDateStamp(hourly.dt, tvHour.context, "ar")
                            tvTimeHours.text = formatTimestamp(hourly.dt, "ar")
                        } else {
                            tvHour.text = formatDateStamp(hourly.dt, tvHour.context, "en")
                            tvTimeHours.text = formatTimestamp(hourly.dt, "en")
                        }
                    }
                }

                lifecycleScope.launch {
                    weatherDataStore.getTemperature.collect { temperature ->
                        when (temperature) {
                            Constants.KELVIN -> tvDegree.text = String.format(
                                "%.0f°${tvDegree.context.getString(R.string.k)}",
                                hourly.temp + 273.15
                            )

                            Constants.FAHRENHEIT -> tvDegree.text = String.format(
                                "%.0f°${tvDegree.context.getString(R.string.f)}",
                                hourly.temp * 9 / 5 + 32
                            )

                            else -> tvDegree.text = String.format(
                                "%.0f°${tvDegree.context.getString(R.string.f)}", hourly.temp
                            )
                        }
                    }
                }
                ivStatusIcon.setIconFromApi(hourly.weather[0].icon)
            }
        }
    }

    object DiffUtils : DiffUtil.ItemCallback<Hourly>() {
        override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
            return oldItem == newItem
        }

    }
}