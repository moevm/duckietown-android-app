package com.etu.duckietownandroid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.etu.duckietownandroid.databinding.FragmentBotControlBinding

/**
 * A simple [Fragment] subclass.
 * Use the [BotControlFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BotControlFragment : Fragment() {
    private var _binding: FragmentBotControlBinding? = null
    private val binding get() = _binding!!

    private var autobot = DeviceItem(0, "Autobot")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            autobot = AppData.autobots[it.getInt("number")]
        }
        (activity as AppCompatActivity?)?.supportActionBar?.title = getString(
            R.string.autobot_info_title,
            autobot.number
        )
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

        // TODO: add control button listeners
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}