package com.etu.duckietownandroid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.etu.duckietownandroid.databinding.FragmentBotControlBinding
import kotlinx.coroutines.*

private const val updateInterval = 1000L

class BotControlFragment : Fragment() {
    private var _binding: FragmentBotControlBinding? = null
    private val binding get() = _binding!!
    private var autobot = DeviceItem(0, "Autobot")
    private var number = 0
    private var updateJob: Job? = null
    private var currentFullStatus = mutableMapOf<StatusKeys, DeviceStatus>()

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
    }

    override fun onStart() {
        super.onStart()
        updateJob = updateAutobot(number + 1, updateInterval)
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