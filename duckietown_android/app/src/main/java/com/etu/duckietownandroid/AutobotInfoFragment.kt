package com.etu.duckietownandroid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.etu.duckietownandroid.databinding.FragmentAutobotInfoBinding
import kotlinx.coroutines.*

private const val updateInterval = 1000L

class AutobotInfoFragment : Fragment() {
    private var _binding: FragmentAutobotInfoBinding? = null
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
        _binding = FragmentAutobotInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.botInfoTable.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.botInfoTable.adapter = AutobotStatusItemAdapter(currentFullStatus)

        // Navigate to bot control
        binding.joystickButton.setOnClickListener {
            val bundle = bundleOf("number" to number)
            safeNavigation(
                findNavController(),
                R.id.action_AutobotInfoFragment_to_fragmentBotControl,
                bundle
            )
        }

        binding.botVideoButton.setOnClickListener {
            val bundle = bundleOf("number" to number, "deviceType" to "autobot")
            safeNavigation(
                findNavController(),
                R.id.action_AutobotInfoFragment_to_imageStreamFragment,
                bundle
            )
        }

        binding.demoButton.setOnClickListener {
            val url = "http://autolab.moevm.info/SOMETHING/autobot${
                String.format(
                    "%02d",
                    number + 1
                )
            }"
            binding.demoButton.isEnabled = false
            startDemo(url)
        }
    }

    override fun onStart() {
        super.onStart()
        updateJob = updateAutobot(number + 1, updateInterval)
        (activity as AppCompatActivity?)?.supportActionBar?.title = getString(
            R.string.autobot_info_title,
            number + 1
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("number", number)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        updateJob?.cancel()
        _binding = null
    }

    private fun updateAutobot(number: Int, delayTime: Long): Job {
        return CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                // Fetch device
                val newAutobot = fetchAutobot(number)

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
                        binding.botInfoTable.adapter?.apply { notifyItemRangeChanged(0, itemCount) }
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

    private fun startDemo(url: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val result = sendRequest(url)
            withContext(Dispatchers.Main) {
                when (result) {
                    true -> Toast.makeText(activity, "Demo started!", Toast.LENGTH_SHORT).show()
                    false -> Toast.makeText(activity, "Demo NOT started!", Toast.LENGTH_SHORT)
                        .show()
                    else -> Toast.makeText(
                        activity,
                        "No internet connection!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                binding.demoButton.isEnabled = true
            }
        }
    }
}