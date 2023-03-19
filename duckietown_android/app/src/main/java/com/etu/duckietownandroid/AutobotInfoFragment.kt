package com.etu.duckietownandroid

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.etu.duckietownandroid.databinding.DialogInfoErrorFragmentBinding
import com.etu.duckietownandroid.databinding.FragmentAutobotInfoBinding
import kotlinx.coroutines.*

class AutobotInfoFragment : DuckieFragment(R.string.how_to_use_autobot_info) {
    private var _binding: FragmentAutobotInfoBinding? = null
    private val binding get() = _binding!!
    private var autobot = DeviceItem(0, "Autobot")
    private var number = 0
    private var updateJob: Job? = null
    private var currentFullStatus = mutableMapOf<StatusKeys, DeviceStatus>()
    private var isDemoStarted = false
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
                R.id.action_AutobotInfoFragment_to_fragmentBotControl,
                bundle
            )
        }

        binding.botVideoButton.setOnClickListener {
            val bundle = bundleOf("number" to number, "deviceType" to "autobot")
            safeNavigation(
                R.id.action_AutobotInfoFragment_to_imageStreamFragment,
                bundle
            )
        }

        binding.demoButton.setOnClickListener {
            val urlToStart = "http://autolab.moevm.info/SOMETHING_TO_START/autobot${
                String.format(
                    "%02d",
                    number + 1
                )
            }"
            val urlToStop = "http://autolab.moevm.info/SOMETHING_TO_STOP/autobot${
                String.format(
                    "%02d",
                    number + 1
                )
            }"

            if (isDemoStarted) {
                demo(urlToStop, Demo.STOP)
            } else {
                demo(urlToStart, Demo.START)
            }
            binding.demoButton.isEnabled = false
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
                        if(!autobot.is_online){
                            DialogInfoErrorFragment(
                                getString(R.string.autobot_offline_message, number, number),
                                getString(R.string.dialog_title_error),
                                R.drawable.sad_duck_animation).show(
                                activity?.supportFragmentManager!!,
                                "info_error")
                            findNavController().navigateUp()
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

    private fun demo(url: String, demo: Demo) {
        var statusStr = ""
        var buttonText = ""
        if (demo == Demo.START) {
            statusStr = "started"
            buttonText = getString(R.string.stop_demo_title)
        } else {
            statusStr = "stopped"
            buttonText = getString(R.string.start_demo_title)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val result = sendRequest(url)
            withContext(Dispatchers.Main) {
                when (result) {
                    true -> {
                        Toast.makeText(activity, "Demo $statusStr!", Toast.LENGTH_SHORT).show()
                        isDemoStarted = !isDemoStarted
                        binding.demoButton.text = buttonText
                    }
                    false -> Toast.makeText(activity, "Demo NOT $statusStr!", Toast.LENGTH_SHORT)
                        .show()
                    else -> DialogInfoErrorFragment(
                                getString(R.string.autobot_start_demo_connection_failed),
                                getString(R.string.dialog_title_error),
                                R.drawable.sad_duck_animation).show(
                        activity?.supportFragmentManager!!,
                        "info_error")
                }
                binding.demoButton.isEnabled = true
            }
        }
    }

}