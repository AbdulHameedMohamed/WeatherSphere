package com.example.weathersphere.view.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weathersphere.databinding.ItemHoursBinding
import com.example.weathersphere.model.data.ForecastItem
import com.example.weathersphere.utils.getDateFromDateText
import com.example.weathersphere.utils.getTimeFromDateText

class HoursAdapter :
    ListAdapter<ForecastItem, HoursAdapter.HourViewHolder>(HourDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val binding = ItemHoursBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        val forecastItem = getItem(position)
        holder.bind(forecastItem)
    }

    class HourViewHolder(private val binding: ItemHoursBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ForecastItem) {
            binding.tvDateHours.text = getDateFromDateText(item.dateTimeText)
            binding.tvTimeHours.text = getTimeFromDateText(item.dateTimeText)
            binding.tvDegreeHours.text = "${item.main.tempKf}°C"

            Glide.with(binding.root)
                .load(getWeatherIconUrl(item.weather.firstOrNull()?.icon))
                .into(binding.ivStatusIconHours)
        }

        private fun getWeatherIconUrl(icon: String?): String {
            return "https://openweathermap.org/img/wn/$icon.png"
        }
    }

    class HourDiffCallback : DiffUtil.ItemCallback<ForecastItem>() {
        override fun areItemsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
            return oldItem == newItem
        }
    }
}