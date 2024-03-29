package com.example.weathersphere.ui.favourite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weathersphere.databinding.ItemFavouriteBinding
import com.example.weathersphere.model.data.Place

class FavouriteAdapter(val onItemClick: (place: Place) -> Unit) :
    ListAdapter<Place, FavouriteAdapter.FavouriteViewHolder>(RecyclerDiffUtilPlace()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val binding = ItemFavouriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavouriteViewHolder(binding)
    }


    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.onBind(currentItem)
    }

    inner class FavouriteViewHolder(private val binding: ItemFavouriteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(currentItem: Place) {

            binding.tvLocation.text = currentItem.cityName
            itemView.setOnClickListener {
                onItemClick(currentItem)
            }
        }
    }


}

class RecyclerDiffUtilPlace : DiffUtil.ItemCallback<Place>() {
    override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean {
        return oldItem == newItem
    }
}