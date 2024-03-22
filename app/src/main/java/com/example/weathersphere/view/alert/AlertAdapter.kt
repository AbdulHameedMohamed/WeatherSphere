package com.example.weathersphere.view.alert

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weathersphere.databinding.ItemAlertBinding
import com.example.weathersphere.model.data.WeatherAlarm
import com.example.weathersphere.utils.formatLongToString

class AlertAdapter :
    ListAdapter<WeatherAlarm, AlertAdapter.AlertViewHolder>(RecyclerDiffUtilAlarmItem()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val binding = ItemAlertBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.onBind(currentItem)
    }

    inner class AlertViewHolder(private val binding: ItemAlertBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(currentItem: WeatherAlarm) {
            binding.apply {
                tvKind.text = currentItem.kind
                tvZoneName.text = currentItem.zoneName

                tvFromDate.text =
                    formatLongToString(currentItem.time, "dd MMM yyyy")

                tvFromTime.text =
                    formatLongToString(currentItem.time, "hh:mm a")
            }
        }
    }
    class RecyclerDiffUtilAlarmItem : DiffUtil.ItemCallback<WeatherAlarm>() {
        override fun areItemsTheSame(oldItem: WeatherAlarm, newItem: WeatherAlarm): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: WeatherAlarm, newItem: WeatherAlarm): Boolean {
            return oldItem == newItem
        }
    }
}