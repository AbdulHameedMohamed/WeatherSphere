package com.example.weathersphere.view.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weathersphere.databinding.ItemDaysBinding
import com.example.weathersphere.model.data.ForecastItem
import com.example.weathersphere.utils.getDayNameFromDate

class DaysAdapter :
    ListAdapter<ForecastItem, DaysAdapter.ForecastItemViewHolder>(DayViewHolder()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastItemViewHolder {
        return ForecastItemViewHolder.createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ForecastItemViewHolder, position: Int) {
        val forecastItem = getItem(position)
        holder.bind(forecastItem)
    }

    class ForecastItemViewHolder(private val binding: ItemDaysBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ForecastItem) {
            binding.tvDayDays.text = getDayNameFromDate(item.dateTimeText)
            binding.tvStatusDays.text = item.weather.firstOrNull()?.description ?: ""
            binding.tvDegreeDays.text = "${item.main.tempMax}/${item.main.tempMin}°C"

            Glide.with(binding.root)
                .load(getWeatherIconUrl(item.weather.firstOrNull()?.icon))
                .into(binding.ivIconDays)
        }

        private fun getWeatherIconUrl(icon: String?): String {
            return "https://openweathermap.org/img/wn/$icon.png"
        }

        companion object {
            fun createViewHolder(parent: ViewGroup): ForecastItemViewHolder {
                val binding = ItemDaysBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ForecastItemViewHolder(binding)
            }
        }
    }

    class DayViewHolder : DiffUtil.ItemCallback<ForecastItem>() {
        override fun areItemsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
            return oldItem == newItem
        }
    }
}