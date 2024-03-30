package com.example.weathersphere.ui.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weathersphere.R
import com.example.weathersphere.databinding.ItemDailyBinding
import com.example.weathersphere.model.data.Daily
import com.example.weathersphere.model.datastore.WeatherDataStore
import com.example.weathersphere.utils.Constants
import com.example.weathersphere.utils.formatDayOfWeek
import com.example.weathersphere.utils.setIconFromApi
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class DailyAdapter(
    private val weatherDataStore: WeatherDataStore,
    private val lifecycleScope: LifecycleCoroutineScope
) : ListAdapter<Daily, DailyAdapter.ViewHolder>(DiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.createDailyViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = getItem(position)

        holder.onBind(day, weatherDataStore, lifecycleScope)
    }

    class ViewHolder(private val binding: ItemDailyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun createDailyViewHolder(parent: ViewGroup): ViewHolder {
                val binding =
                    ItemDailyBinding.inflate(LayoutInflater.from(parent.context), parent, false)

                return ViewHolder(binding)
            }
        }

        fun onBind(
            day: Daily,
            weatherDataStore: WeatherDataStore,
            lifecycleScope: LifecycleCoroutineScope
        ) {
            binding.apply {

                lifecycleScope.launch {
                    weatherDataStore.getLanguage.collect { language ->
                        if (language == Constants.ARABIC)
                            tvDayName.text =
                                formatDayOfWeek(day.dt, binding.tvDayName.context, "ar")
                        else
                            tvDayName.text =
                                formatDayOfWeek(day.dt, binding.tvDayName.context, "en")
                    }
                }

                tvDayStatus.text = day.weather[0].description
                ivDayIcon.setIconFromApi(day.weather[0].icon)

                lifecycleScope.launch {
                    weatherDataStore.getTemperature.collect { temperature ->
                        when (temperature) {
                            Constants.KELVIN -> tvDayDegree.text = String.format(
                                "%.0f/%.0f°${tvDayDegree.context.getString(R.string.k)}",
                                day.temp.max + 273.15, day.temp.min + 273.15
                            )

                            Constants.FAHRENHEIT -> tvDayDegree.text = String.format(
                                "%.0f/%.0f°${tvDayDegree.context.getString(R.string.f)}",
                                day.temp.max * 9 / 5 + 32, day.temp.min * 9 / 5 + 32
                            )

                            else -> tvDayDegree.text =
                                String.format(
                                    "%.0f/%.0f°${tvDayDegree.context.getString(R.string.c)}",
                                    day.temp.max,
                                    day.temp.min
                                )
                        }
                    }
                }
            }
        }
    }

    object DiffUtils : DiffUtil.ItemCallback<Daily>() {
        override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
            return oldItem == newItem
        }
    }
}