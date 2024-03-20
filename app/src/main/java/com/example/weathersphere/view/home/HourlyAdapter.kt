package com.example.weathersphere.view.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weathersphere.databinding.ItemHoursBinding
import com.example.weathersphere.model.data.Hourly
import com.example.weathersphere.utils.formatDateStamp
import com.example.weathersphere.utils.formatTimestamp
import com.example.weathersphere.utils.setIconFromApi
import kotlin.math.roundToInt

class HourlyAdapter :
    ListAdapter<Hourly, HourlyAdapter.ViewHolder>(DiffUtils) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.createHourlyViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hourly = getItem(position)
        holder.onBind(hourly)
    }

    class ViewHolder(private val binding: ItemHoursBinding) :
        RecyclerView.ViewHolder(binding.root) {
            companion object {
                fun createHourlyViewHolder(parent: ViewGroup): ViewHolder {
                    val binding = ItemHoursBinding.inflate(LayoutInflater.from(parent.context), parent, false)

                    return ViewHolder(binding)
                }
            }
        fun onBind(hourly: Hourly) {
            binding.apply {
                tvHour.text = formatDateStamp(hourly.dt, tvHour.context, "en")
                tvTimeHours.text = formatTimestamp(hourly.dt, "en")

                binding.tvDegree.text = "${hourly.temp.roundToInt()}°C"

                binding.ivStatusIcon.setIconFromApi(hourly.weather[0].icon)
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