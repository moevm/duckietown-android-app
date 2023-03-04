package com.etu.duckietownandroid

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.etu.duckietownandroid.databinding.FragmentListBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class DeviceListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private var data = MutableList<DeviceItem>(0){ DeviceItem(0, "name") }
    private var itemListener = {position: Int -> adapterOnItemClick(position)}

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
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
        (activity as AppCompatActivity?)?.supportActionBar?.subtitle = ""
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.menu_filter)
        item.isVisible = true
        item.setOnMenuItemClickListener {
            val filter = DialogFilterFragment()
            filter.show(activity?.supportFragmentManager!!, "dialog")
            true
        }
    }

    private fun adapterOnItemClick(position: Int){
        val item = data[position]
        Toast.makeText(activity, item.name, Toast.LENGTH_SHORT).show()
    }

    private fun adapterOnAutobotItemClick(position: Int){
        val bundle = bundleOf("number" to position)
        safeNavigation(findNavController(), R.id.action_ListFragment_to_AutobotInfoFragment, bundle)
    }

    private fun adapterOnWatchtowerItemClick(position: Int) {
        val bundle = bundleOf("number" to position, "deviceType" to "watchtower")
        safeNavigation(findNavController(), R.id.action_ListFragment_to_imageStreamFragment, bundle)
    }

    private fun adapterOnCameraItemClick(position: Int) {
        val bundle = bundleOf("number" to position)
        safeNavigation(findNavController(), R.id.action_ListFragment_to_cameraFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}