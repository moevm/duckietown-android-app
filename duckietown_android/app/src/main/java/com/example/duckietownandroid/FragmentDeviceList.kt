package com.example.duckietownandroid

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.duckietownandroid.databinding.FragmentListBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class FragmentDeviceList : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val data = MutableList(27) { index -> "Autobot $index" }

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

        binding.deviceListHeader.text = getString(
            R.string.device_list_status,
            AppData.autobots.count(){item -> item.is_online},
            AppData.autobots.size)
        val adapter = DeviceAdapter(AppData.autobots) { item -> adapterOnItemClick(item) }
        val recycleView = binding.deviceList
        recycleView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recycleView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        recycleView.adapter = adapter

    }

    private fun adapterOnItemClick(item: DeviceItem){
        Toast.makeText(activity, item.name, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}