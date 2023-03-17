package com.etu.duckietownandroid

import android.os.Bundle
import android.util.Log
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
 * Use the [ImageStreamFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ImageStreamFragment : DuckieFragment(R.string.how_to_use_image_stream) {
    // TODO: Rename and change types of parameters
    private var device = DeviceItem(0, "Watchtower")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            when (it.getString("deviceType")) {
                "autobot" -> device = AppData.autobots[it.getInt("number")]
                "watchtower" -> device = AppData.watchtowers[it.getInt("number")]
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_stream, container, false)
    }

    override fun onStart() {
        super.onStart()

        (activity as AppCompatActivity?)?.supportActionBar?.title = device.name
        Log.d("DEVICE", device.name)
        (activity as AppCompatActivity?)?.supportActionBar?.subtitle = when(device.is_online){
            true -> "Online"
            else -> "Offline"
        }
    }

}