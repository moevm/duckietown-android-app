package com.etu.duckietownandroid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.etu.duckietownandroid.databinding.FragmentCameraBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CameraFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private var camera = DeviceItem(0, "Camera")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            camera = AppData.cameras[it.getInt("number")]
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        (activity as AppCompatActivity?)?.supportActionBar?.title = getString(
            R.string.camera_info_title,
            camera.number
        )
        (activity as AppCompatActivity?)?.supportActionBar?.subtitle = when(camera.is_online){
            true -> "Online"
            else -> "Offline"
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.leftButton.setOnClickListener {
            if((camera.number - 1) == 0) {
                camera = AppData.cameras[AppData.cameras.size - 1]
            } else {
                camera = AppData.cameras[camera.number - 2]
            }

            (activity as AppCompatActivity?)?.supportActionBar?.title = getString(
                R.string.camera_info_title,
                camera.number
            )
            (activity as AppCompatActivity?)?.supportActionBar?.subtitle = when(camera.is_online){
                true -> "Online"
                else -> "Offline"
            }
        }

        binding.rightButton.setOnClickListener {
            if(camera.number == AppData.cameras.size) {
                camera = AppData.cameras[0]
            } else {
                camera = AppData.cameras[camera.number]
            }

            (activity as AppCompatActivity?)?.supportActionBar?.title = getString(
                R.string.camera_info_title,
                camera.number
            )
            (activity as AppCompatActivity?)?.supportActionBar?.subtitle = when(camera.is_online){
                true -> "Online"
                else -> "Offline"
            }
        }
    }
}