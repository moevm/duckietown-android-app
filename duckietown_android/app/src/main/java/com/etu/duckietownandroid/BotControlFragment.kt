package com.etu.duckietownandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.etu.duckietownandroid.databinding.FragmentBotControlBinding
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.Date

private const val MAX_PRESS_LOGS_LENGTH = 30

class BotControlFragment : DuckieFragment(R.string.how_to_use_autobot_control) {
    private var _binding: FragmentBotControlBinding? = null
    private val binding get() = _binding!!
    private var autobot = DeviceItem(0, "Autobot")
    private var number = 0
    private var updateJob: Job? = null
    private var currentFullStatus = mutableMapOf<StatusKeys, DeviceStatus>()
    private var buttonPressLogs = MutableList(0) {""}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            number = it.getInt("number")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBotControlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.botInfoJoystic.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.botInfoJoystic.adapter = AutobotStatusItemAdapter(
            currentFullStatus,
            arrayOf(StatusKeys.BATTERY, StatusKeys.TEMPERATURE, StatusKeys.CPU)
        )

        // Adapter for logs
        binding.LogsRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, true)
        binding.LogsRecyclerView.adapter = LogAdapter(buttonPressLogs)

        // Listeners for button presses
        binding.forwardButton.setOnClickListener {
            val time= SimpleDateFormat("HH:mm:ss", resources.configuration.locale).format(Date())
            addPressLog("[$time] Forward pressed")
        }
        binding.backwardButton.setOnClickListener {
            val time= SimpleDateFormat("HH:mm:ss", resources.configuration.locale).format(Date())
            addPressLog("[$time] Backward pressed")
        }
        binding.leftButton.setOnClickListener {
            val time= SimpleDateFormat("HH:mm:ss", resources.configuration.locale).format(Date())
            addPressLog("[$time] Left pressed")
        }
        binding.rightButton.setOnClickListener {
            val time= SimpleDateFormat("HH:mm:ss", resources.configuration.locale).format(Date())
            addPressLog("[$time] Right pressed")
        }
    }

    private fun addPressLog(msg: String){
        if(buttonPressLogs.size >= MAX_PRESS_LOGS_LENGTH){
            buttonPressLogs.removeLast()
            buttonPressLogs.add(0, msg)
            binding.LogsRecyclerView.adapter?.notifyItemRemoved(buttonPressLogs.size - 1)
            binding.LogsRecyclerView.adapter?.notifyItemInserted(0)
            binding.LogsRecyclerView.scrollToPosition(0)
        }else{
            buttonPressLogs.add(0, msg)
            binding.LogsRecyclerView.adapter?.notifyItemInserted(0)
            binding.LogsRecyclerView.scrollToPosition(0)
        }
    }

    override fun onStart() {
        super.onStart()
        updateJob = updateAutobot(number + 1, getUpdateTime(activity))
        (activity as AppCompatActivity?)?.supportActionBar?.title = getString(
            R.string.autobot_info_title,
            number + 1
        )
    }

    private fun updateAutobot(number: Int, delayTime: Long): Job {
        return CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                // Fetch device
                val newAutobot = context?.let { LabRequests(it).fetchAutobot(number) }

                // Update UI
                withContext(Dispatchers.Main) {
                    if (newAutobot != null) {
                        // Update bot info
                        autobot = newAutobot

                        (activity as AppCompatActivity?)?.supportActionBar?.subtitle =
                            when (autobot.is_online) {
                                true -> "Online"
                                else -> "Offline"
                            }

                        currentFullStatus.putAll(autobot.fullStatus)
                        binding.botInfoJoystic.adapter?.apply { notifyItemRangeChanged(0, itemCount) }
                    } else {
                        // No internet connection
                        (activity as AppCompatActivity?)?.supportActionBar?.subtitle =
                            "No connection"
                    }
                }
                delay(delayTime)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        updateJob?.cancel()
        _binding = null
    }

}