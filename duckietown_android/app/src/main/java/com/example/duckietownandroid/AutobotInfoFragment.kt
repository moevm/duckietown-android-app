package com.example.duckietownandroid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AutobotInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AutobotInfoFragment : Fragment() {
    // TODO: Rename and change types of parameters
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
        (activity as AppCompatActivity?)?.supportActionBar?.subtitle = when(autobot.is_online){
            true -> "Online"
            else -> "Offline"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_autobot_info, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as AppCompatActivity?)?.supportActionBar?.subtitle = ""
    }

}