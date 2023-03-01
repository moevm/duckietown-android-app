package com.example.duckietownandroid

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.duckietownandroid.databinding.FragmentListBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class FragmentDeviceList : Fragment() {

    private var _binding: FragmentListBinding? = null
    private var data = MutableList<DeviceItem>(0){ DeviceItem(0, "name") }
    private var itemListener = {position: Int -> adapterOnItemClick(position)}

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val n = arguments?.getInt("number") ?: -1
        Log.d("UI", "OnCreateView: arg number is $n")
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val typeName = arguments?.getString("list_type") ?: "unknown"
        setCurrentDevices(typeName)

        binding.deviceListHeader.text = getString(
            R.string.device_list_status,
            data.count(){item -> item.is_online},
            data.size)
        val adapter = DeviceAdapter(data, itemListener)
        val recycleView = binding.deviceList
        recycleView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recycleView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        recycleView.adapter = adapter

    }

    private fun setCurrentDevices(type_name: String){
        data = when(type_name){
            "autobots" -> AppData.autobots
            "watchtowers" -> AppData.watchtowers
            "cameras" -> AppData.cameras
            else -> data
        }
        itemListener = when(type_name){
            "autobots" -> {position: Int -> adapterOnAutobotItemClick(position)}
            "watchtowers" -> {position: Int -> adapterOnWatchtowerItemClick(position)}
            "cameras" -> {position: Int -> adapterOnCameraItemClick(position)}
            else -> itemListener
        }
        val titleName = when(type_name){
            "autobots" -> getString(R.string.autobots_title)
            "watchtowers" -> getString(R.string.watchtowers_title)
            "cameras" -> getString(R.string.cameras_title)
            else -> "Unknown Title"
        }
        (activity as AppCompatActivity?)?.supportActionBar?.title = titleName
    }

    private fun adapterOnItemClick(position: Int){
        val item = data[position]
        Toast.makeText(activity, item.name, Toast.LENGTH_SHORT).show()
    }

    private fun adapterOnAutobotItemClick(position: Int){
        val bundle = bundleOf("number" to position)
        findNavController().navigate(R.id.action_ListFragment_to_AutobotInfoFragment, bundle)
    }

    private fun adapterOnWatchtowerItemClick(position: Int) {
        val bundle = bundleOf("number" to position)
        findNavController().navigate(R.id.action_ListFragment_to_watchtowerFragment, bundle)
    }

    private fun adapterOnCameraItemClick(position: Int) {
        val bundle = bundleOf("number" to position)
        findNavController().navigate(R.id.action_ListFragment_to_cameraFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}