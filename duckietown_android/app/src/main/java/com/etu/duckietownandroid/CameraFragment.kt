package com.etu.duckietownandroid
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.etu.duckietownandroid.databinding.FragmentCameraBinding
import com.longdo.mjpegviewer.MjpegView


class CameraFragment : DuckieFragment(R.string.how_to_use_camera) {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private var camera = DeviceItem(0, "Camera")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            camera = AppData.cameras[it.getInt("number")]
        }
        setHasOptionsMenu(true)
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
            binding.mjpegview.stopStream()
            loadIpCam()
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
            binding.mjpegview.stopStream()
            loadIpCam()
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

    private fun loadIpCam() {
        if (context == null) {
            return
        }
        val cameraUrl = LabRequests(context!!).getCameraUrl(camera.number) ?: return
        val viewer: MjpegView = binding.mjpegview
        viewer.mode = MjpegView.MODE_BEST_FIT
        viewer.isAdjustHeight = true
        viewer.supportPinchZoomAndPan = true
        viewer.setUrl(cameraUrl)
        viewer.startStream()
        Log.d("Camera", "loadIpCam finished")
    }

    override fun onResume() {
        super.onResume()
        loadIpCam()
    }

    override fun onPause() {
        super.onPause()
        binding.mjpegview.stopStream()
    }

}