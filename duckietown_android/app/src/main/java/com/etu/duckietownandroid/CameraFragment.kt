package com.etu.duckietownandroid

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.etu.duckietownandroid.databinding.FragmentCameraBinding
import com.github.niqdev.mjpeg.DisplayMode
import com.github.niqdev.mjpeg.Mjpeg
import com.github.niqdev.mjpeg.MjpegInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
    private val TIMEOUT = 5
    private var mjpgStream: Mjpeg? = null

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
            binding.leftButton.isEnabled = false
            binding.rightButton.isEnabled = false

            if((camera.number - 1) == 0) {
                camera = AppData.cameras[AppData.cameras.size - 1]
            } else {
                camera = AppData.cameras[camera.number - 2]
            }
            mjpgStream?.sendConnectionCloseHeader()
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
            binding.leftButton.isEnabled = false
            binding.rightButton.isEnabled = false

            if(camera.number == AppData.cameras.size) {
                camera = AppData.cameras[0]
            } else {
                camera = AppData.cameras[camera.number]
            }
            mjpgStream?.sendConnectionCloseHeader()
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
        mjpgStream = Mjpeg.newInstance()
        mjpgStream?.open(cameraUrl, TIMEOUT)
            ?.subscribe(
                { inputStream: MjpegInputStream ->
                    Log.d("Camera", "opens")
                    binding.mjpegViewDefault.setSource(inputStream)
                    binding.mjpegViewDefault.setDisplayMode(DisplayMode.BEST_FIT)
                    binding.mjpegViewDefault.showFps(true)
                    binding.leftButton.isEnabled = true
                    binding.rightButton.isEnabled = true
                }
            ) { throwable: Throwable ->
                Log.e(javaClass.simpleName, "mjpeg error", throwable)
            }
        Log.d("Camera", "loadIpCam finished")

    }

    override fun onResume() {
        super.onResume()
        loadIpCam()
    }

    override fun onPause() {
        super.onPause()
        GlobalScope.launch(Dispatchers.IO) {
            binding.mjpegViewDefault.stopPlayback()

        }

    }

}