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
import com.etu.duckietownandroid.databinding.FragmentAutobotInfoBinding
import kotlinx.coroutines.*


/**
 * A simple [Fragment] subclass.
 * Use the [AutobotInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AutobotInfoFragment : Fragment() {

    private var _binding: FragmentAutobotInfoBinding? = null
    private val binding get() = _binding!!

    private var autobot = DeviceItem(0, "Autobot")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            autobot = AppData.autobots[it.getInt("number")]
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

        // Navigate to bot control
        binding.joystickButton.setOnClickListener {
            val bundle = bundleOf("number" to autobot.number-1)
            safeNavigation(
                findNavController(),
                R.id.action_AutobotInfoFragment_to_fragmentBotControl,
                bundle
            )
        }

        binding.botVideoButton.setOnClickListener {
            val bundle = bundleOf("number" to autobot.number - 1, "deviceType" to "autobot")
            safeNavigation(findNavController(), R.id.action_AutobotInfoFragment_to_imageStreamFragment, bundle)
        }

        binding.demoButton.setOnClickListener {
            val url = "http://autolab.moevm.info/SOMETHING/autobot${String.format("%02d", autobot.number)}"
            binding.demoButton.isEnabled = false
            startDemo(url)
        }
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity?)?.supportActionBar?.title = getString(
            R.string.autobot_info_title,
            autobot.number
        )
        (activity as AppCompatActivity?)?.supportActionBar?.subtitle = when (autobot.is_online) {
            true -> "Online"
            else -> "Offline"
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("number", autobot.number-1)
    }

    private fun startDemo(url: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val result = sendRequest(url)
            withContext(Dispatchers.Main) {
                when (result) {
                    true -> Toast.makeText(activity, "Demo started!", Toast.LENGTH_SHORT).show()
                    false -> Toast.makeText(activity, "Demo NOT started!", Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(activity, "No internet connection!", Toast.LENGTH_SHORT).show()
                }
                binding.demoButton.isEnabled = true
            }
        }
    }
}