package com.etu.duckietownandroid

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.etu.duckietownandroid.databinding.FragmentListBinding
import com.google.android.material.chip.Chip
import kotlinx.coroutines.*

private const val LOW_BATTERY_VALUE = 10
private const val OVERHEAT_VALUE = 100
private const val LOW_MEMORY_VALUE = 0.5

class DeviceListFragment : DuckieFragment(R.string.how_to_use_device_list), DialogFilterFragment.DeviceFilterInterface {

    private var _binding: FragmentListBinding? = null
    private var data = MutableList<DeviceItem>(0) { DeviceItem(0, "name") }
    private var filteredData = MutableList<DeviceItem>(0) { DeviceItem(0, "name") }
    private var itemListener = { position: Int -> adapterOnItemClick(position) }
    private var filter = DeviceFilter()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val tagFilters = mapOf(
        "Online" to { is_checked: Boolean ->
            //TODO call filter
            Log.d("FILTER", "Online filter called, checked: $is_checked")
            filter.onlyOnline = is_checked
            applyFilter()
        },
        "Low battery" to {is_checked ->
            //TODO call filter
            Log.d("FILTER", "Low battery filter called, checked: $is_checked")
            if(is_checked){
                filter.addConstraint(StatusKeys.BATTERY, null, LOW_BATTERY_VALUE.toDouble())
            }else{
                filter.removeConstraint(StatusKeys.BATTERY)
            }
            applyFilter()
        },
        "Overheat" to {is_checked ->
            //TODO call filter
            Log.d("FILTER", "Overheat filter called, checked: $is_checked")
            if(is_checked){
                filter.addConstraint(StatusKeys.TEMPERATURE, OVERHEAT_VALUE.toDouble(), null)
            }else{
                filter.removeConstraint(StatusKeys.TEMPERATURE)
            }
            applyFilter()
        },
        "Low memory" to {is_checked ->
            //TODO call filter
            Log.d("FILTER", "Low memory filter called, checked: $is_checked")
            if(is_checked){
                filter.addConstraint(StatusKeys.MEMORY, null, LOW_MEMORY_VALUE)
            }else{
                filter.removeConstraint(StatusKeys.MEMORY)
            }
            applyFilter()
        }
    )
    private val autobotFilters = listOf("Online", "Low battery", "Overheat", "Low memory")
    private val watchtowerFilters = listOf("Online")
    private val cameraFilters = listOf("Online")
    private var updateJob: Job? = null

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

        binding.deviceList.addItemDecoration(
            DividerItemDecoration(
                activity,
                LinearLayoutManager.VERTICAL
            )
        )
        binding.deviceList.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        val adapter = DeviceAdapter(filteredData, itemListener)
        binding.deviceList.adapter = adapter

        updateJob = updateDevices(typeName, getUpdateTime(activity))

        val filterList: List<String> =
            when (typeName) {
                "autobots" -> autobotFilters
                "watchtowers" -> watchtowerFilters
                "cameras" -> cameraFilters
                else -> listOf()
            }

        filterList.forEach { name ->
            val chip = Chip(context)
            chip.text = name
            chip.isCheckable = true
            chip.setOnCheckedChangeListener{_, is_checked ->
                tagFilters[name]?.let { it1 -> it1(is_checked) }
            }
            binding.filterGroup.addView(chip)
        }
    }

    private fun updateDevices(type_name: String, delayTime: Long): Job {
        return CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {

                // Fetch devices
                val newData = let{
                    try{
                        context?.let { LabRequests(it).fetchDevices(type_name) }
                    } catch (err: IllegalArgumentException){
                        // User set invalid url
                        DialogInfoErrorFragment(
                            getString(R.string.dialog_info_error_bad_url),
                            getString(R.string.dialog_title_error),
                            R.drawable.sad_duck_animation).show(
                            activity?.supportFragmentManager!!,
                            "info_error")
                        cancel()
                        null
                    }
                }
                if (newData == null) {
                    delay(delayTime)
                    continue
                }
                data.clear()
                data.addAll(newData)

                // Update UI
                withContext(Dispatchers.Main) {
                    applyFilter()
                }

                delay(delayTime)
            }
        }
    }

    private fun setCurrentDevices(type_name: String) {
        itemListener = when (type_name) {
            "autobots" -> { position: Int -> adapterOnAutobotItemClick(position) }
            "watchtowers" -> { position: Int -> adapterOnWatchtowerItemClick(position) }
            "cameras" -> { position: Int -> adapterOnCameraItemClick(position) }
            else -> itemListener
        }
        val titleName = when (type_name) {
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
            val filter = DialogFilterFragment(this)
            filter.show(activity?.supportFragmentManager!!, "dialog")
            true
        }
    }

    private fun adapterOnItemClick(position: Int) {
        val item = data[position]
        Toast.makeText(activity, item.name, Toast.LENGTH_SHORT).show()
    }

    private fun adapterOnAutobotItemClick(position: Int) {
        val bundle = bundleOf("number" to position)
        safeNavigation(R.id.action_ListFragment_to_AutobotInfoFragment, bundle)
    }

    private fun adapterOnWatchtowerItemClick(position: Int) {
        val bundle = bundleOf("number" to position, "deviceType" to "watchtower")
        safeNavigation(R.id.action_ListFragment_to_imageStreamFragment, bundle)
    }

    private fun adapterOnCameraItemClick(position: Int) {
        val bundle = bundleOf("number" to position)
        safeNavigation(R.id.action_ListFragment_to_cameraFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        updateJob?.cancel()
        _binding = null
    }

    override fun getFilter(): DeviceFilter = filter

    override fun applyFilter(newData: MutableList<DeviceItem>?) {
        val newFilteredData = filter.applyFilter(data)
        filteredData.clear()
        filteredData.addAll(newFilteredData)
        updateList()
    }

    private fun updateList(){
        binding.deviceListHeader.text = getString(
            R.string.device_list_status,
            filteredData.count() { item -> item.is_online },
            filteredData.size
        )
        binding.deviceList.adapter?.notifyDataSetChanged()
    }
}