package com.example.weathersphere.view.alert

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.weathersphere.R
import com.example.weathersphere.databinding.DialogAlertBinding
import com.example.weathersphere.databinding.FragmentAlertBinding
import com.example.weathersphere.model.LocationDataStore
import com.example.weathersphere.model.WeatherRepository
import com.example.weathersphere.model.WeatherResult
import com.example.weathersphere.model.data.WeatherAlarm
import com.example.weathersphere.model.local.DatabaseProvider
import com.example.weathersphere.model.local.WeatherLocalDataSource
import com.example.weathersphere.model.remote.RetrofitClient
import com.example.weathersphere.model.remote.WeatherRemoteDataSource
import com.example.weathersphere.utils.Constants
import com.example.weathersphere.utils.NotificationManager
import com.example.weathersphere.utils.checkNotificationPermission
import com.example.weathersphere.utils.formatHourMinuteToString
import com.example.weathersphere.utils.formatMillisToDateTimeString
import com.example.weathersphere.utils.dateTimeStringToMillis
import com.example.weathersphere.utils.requestNotificationPermission
import com.example.weathersphere.utils.setLocationNameByGeoCoder
import com.example.weathersphere.utils.showToast
import com.example.weathersphere.viewmodel.AlertViewModel
import com.example.weathersphere.work.AlarmScheduler
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class AlertFragment : Fragment() {
    private lateinit var binding: FragmentAlertBinding
    private lateinit var dialogAlertBinding: DialogAlertBinding
    private lateinit var viewModel: AlertViewModel
    private lateinit var adapter: AlertAdapter
    private val locationDataStore by lazy {
        LocationDataStore(requireContext())
    }

    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private var currentZoneName = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationManager.createNotificationChannel(requireContext())
        setupViewModel()
        setupRecyclerView()
        setListeners()

        viewModel.getAllAlarms()
        viewModel.getWeather()

        observeStateFlow()
    }

    private fun setListeners() {
        binding.fabAddAlert.setOnClickListener {
            lifecycleScope.launch {
                if (isNotificationEnabled()) {
                    if (checkNotificationPermission(requireContext())) {
                        showWeatherAlertDialog()
                    } else {
                        requestNotificationPermission(requireActivity())
                    }
                } else {
                    requireContext().showToast("You need to enable notifications from settings first.")
                }
            }
        }
    }

    private fun observeStateFlow() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.alarmsStateFlow.collectLatest {
                    adapter.submitList(it)
                }

                viewModel.weatherFlow.collectLatest {
                    if (it is WeatherResult.Success) {
                        currentLatitude = it.data.lat
                        currentLongitude = it.data.lon
                        currentZoneName = it.data.timezone
                        try {
                            currentZoneName = setLocationNameByGeoCoder(it.data, requireContext())
                        } catch (_: Exception) {
                        }
                    }
                }
            }
        }
    }

    private fun showWeatherAlertDialog() {
        val currentTimeInMillis = System.currentTimeMillis()

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialogAlertBinding = DialogAlertBinding.inflate(layoutInflater)
        dialog.setContentView(dialogAlertBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogAlertBinding.tvFromDateDialog.text =
            formatMillisToDateTimeString(currentTimeInMillis, "dd MMM yyyy")
        dialogAlertBinding.tvFromTimeDialog.text =
            formatMillisToDateTimeString(currentTimeInMillis + 60 * 1000, "hh:mm a")

        dialogAlertBinding.cvAlarmTime.setOnClickListener {
            showDatePicker()
        }

        dialogAlertBinding.btnSaveDialog.setOnClickListener {

            val time = dateTimeStringToMillis(
                dialogAlertBinding.tvFromDateDialog.text.toString(),
                dialogAlertBinding.tvFromTimeDialog.text.toString()
            )

            if (time > currentTimeInMillis) {
                val kindId = dialogAlertBinding.radioGroupAlertDialog.checkedRadioButtonId

                val kind =
                    if (kindId == dialogAlertBinding.radioAlert.id)
                        Constants.ALERT
                    else Constants.NOTIFICATION

                val weatherAlarm =
                    WeatherAlarm(time, kind, currentLatitude, currentLongitude, currentZoneName)

                if (kind == Constants.ALERT && !Settings.canDrawOverlays(requireContext())) {
                        requestOverlayPermission()
                        return@setOnClickListener
                }

                viewModel.createAlarm(weatherAlarm)

                dialog.dismiss()
            } else {
                Toast.makeText(
                    requireContext(),
                    "please select time in the future",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialog.show()
    }

    private fun showDatePicker() {
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder.build())
                .setTitleText("Select Date")
                .build()

        datePicker.show(parentFragmentManager, "date")

        datePicker.addOnPositiveButtonClickListener { date ->
            dialogAlertBinding.tvFromDateDialog.text =
                formatMillisToDateTimeString(date, "dd MMM yyyy")
            showTimePicker()
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val timePicker =
            MaterialTimePicker.Builder()
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(currentHour)
                .setMinute(currentMinute + 1)
                .setTitleText("Select Appointment time")
                .build()

        timePicker.show(parentFragmentManager, "time")
        timePicker.addOnPositiveButtonClickListener {
            dialogAlertBinding.tvFromTimeDialog.text =
                formatHourMinuteToString(timePicker.hour, timePicker.minute)
        }

        timePicker.addOnCancelListener {
            dialogAlertBinding.tvFromTimeDialog.text =
                formatHourMinuteToString(timePicker.hour, timePicker.minute)
        }
    }

    private fun requestNotification() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:com.example.weathersphere")
        startActivity(intent)
    }

    private fun requestOverlayPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:com.example.weathersphere")
        startActivity(intent)
    }

    private suspend fun isNotificationEnabled(): Boolean {
        var isNotificationEnabled = false
        locationDataStore.isNotificationEnabled.collect {
            isNotificationEnabled = it
        }
        return isNotificationEnabled
    }

    private fun setupViewModel() {
        val productsApi = WeatherRemoteDataSource(RetrofitClient.apiService)
        val productDao =
            WeatherLocalDataSource(DatabaseProvider.getDatabase(requireContext()).weatherDao)
        val repository = WeatherRepository.getInstance(productsApi, productDao)
        val viewModelFactory =
            AlertViewModel.Factory(repository, AlarmScheduler.getInstance(requireActivity().application))
        viewModel = ViewModelProvider(this, viewModelFactory)[AlertViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = AlertAdapter()
        binding.rvAlerts.adapter = adapter
        deleteBySwipe()
    }

    private fun deleteBySwipe() {
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
                val weatherAlarm = adapter.currentList[position]

                val mediaPlayer = MediaPlayer.create(context, R.raw.deleted)
                mediaPlayer.start()
                mediaPlayer.setOnCompletionListener { mp ->
                    mp.release()
                }

                viewModel.destroyAlarm(weatherAlarm)

                Snackbar.make(requireView(), "Alarm Deleted.... ", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.insertAlarm(weatherAlarm)
                        viewModel.createAlarmScheduler(weatherAlarm)
                    }
                    show()
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(binding.rvAlerts)
    }
}