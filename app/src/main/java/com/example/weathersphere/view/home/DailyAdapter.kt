package com.example.weathersphere.view.home
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weathersphere.R
import com.example.weathersphere.databinding.ItemDailyBinding
import com.example.weathersphere.model.data.Daily
import com.example.weathersphere.utils.formatDayOfWeek
import com.example.weathersphere.utils.setIconFromApi
import java.util.*
import kotlin.math.roundToInt

class DailyAdapter : ListAdapter<Daily, DailyAdapter.ViewHolder>(DiffUtils) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.createDailyViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = getItem(position)

        holder.onBind(day)
    }

    class ViewHolder(private val binding: ItemDailyBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun createDailyViewHolder(parent: ViewGroup): ViewHolder {
                val binding = ItemDailyBinding.inflate(LayoutInflater.from(parent.context), parent, false)

                return ViewHolder(binding)
            }
        }
        fun onBind(day: Daily){
            binding.apply {
                when (adapterPosition) {
                    0 -> {
                        tvDayName.text = binding.root.context.getString(R.string.today)
                    }
                    1 -> {
                        tvDayName.text = binding.root.context.getString(R.string.tomorrow)
                    }
                    else -> {
                        // Make It Dynamic en
                        tvDayName.text = formatDayOfWeek(day.dt, binding.tvDayName.context, "en")
                    }
                }

                tvDayStatus.text = day.weather[0].description
                ivDayIcon.setIconFromApi(day.weather[0].icon)

                tvDayDegree.text = "${day.temp.day.roundToInt()} °C"
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