package com.example.weathersphere.view.favourite

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.weathersphere.R
import com.example.weathersphere.databinding.FragmentFavouriteBinding
import com.example.weathersphere.model.WeatherRepository
import com.example.weathersphere.model.local.DatabaseProvider
import com.example.weathersphere.model.local.WeatherLocalDataSource
import com.example.weathersphere.model.remote.RetrofitClient
import com.example.weathersphere.model.remote.WeatherRemoteDataSource
import com.example.weathersphere.utils.Constants
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar

class FavouriteFragment : Fragment() {
    private lateinit var binding: FragmentFavouriteBinding
    lateinit var favouriteAdapter: FavouriteAdapter
    lateinit var favouriteViewModel: FavouriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)

        initRecyclerView()

        setListeners()

        setupViewModel()

        return binding.root
    }

    private fun initRecyclerView() {
        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val place = favouriteAdapter.currentList[position]
                val mediaPlayer = MediaPlayer.create(context, R.raw.deleted)
                mediaPlayer.start()

                mediaPlayer.setOnCompletionListener { mp ->
                    mp.release()
                }
                favouriteViewModel.deletePlaceFromFav(place)
                Snackbar.make(
                    requireView(),
                    "deleting Location ${place.cityName}",
                    Snackbar.LENGTH_LONG
                ).apply {
                    setAction("Undo") {
                        favouriteViewModel.insertPlaceToFavourite(place)
                    }
                    show()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(binding.rvFavourite)

        favouriteAdapter = FavouriteAdapter {
            // Navigate To Home Screen
            Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
        binding.rvFavourite.adapter = favouriteAdapter
    }

    private fun setupViewModel() {
        val productsApi = WeatherRemoteDataSource(RetrofitClient.apiService)
        val productDao =
            WeatherLocalDataSource(DatabaseProvider.getDatabase(requireContext()).weatherDao)
        val repository = WeatherRepository.getInstance(productsApi, productDao)

        val factory = FavouriteViewModel.Factory(repository)
        favouriteViewModel = ViewModelProvider(this, factory)[FavouriteViewModel::class.java]
    }

    private fun setListeners() {
        binding.fabAdd.setOnClickListener {
            val action =
                FavouriteFragmentDirections.actionFavouriteFragmentToMapFragment(Constants.FAVOURITE)
            findNavController().navigate(action)
        }
    }
}