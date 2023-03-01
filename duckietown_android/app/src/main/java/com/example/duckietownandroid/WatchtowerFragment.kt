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
 * Use the [WatchtowerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WatchtowerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var watchtower = DeviceItem(0, "Watchtower")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            watchtower = AppData.watchtowers[it.getInt("number")]
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_watchtower, container, false)
    }

    override fun onStart() {
        super.onStart()

        (activity as AppCompatActivity?)?.supportActionBar?.title = getString(
            R.string.watchtower_info_title,
            watchtower.number
        )
        (activity as AppCompatActivity?)?.supportActionBar?.subtitle = when(watchtower.is_online){
            true -> "Online"
            else -> "Offline"
        }
    }

}