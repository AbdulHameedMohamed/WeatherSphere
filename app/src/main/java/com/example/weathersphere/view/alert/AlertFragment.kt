package com.example.weathersphere.view.alert

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weathersphere.R
import com.example.weathersphere.databinding.FragmentAlertBinding
import com.example.weathersphere.databinding.FragmentFavouriteBinding

class AlertFragment : Fragment() {
    private lateinit var binding: FragmentAlertBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertBinding.inflate(inflater, container, false)

        return binding.root
    }
}