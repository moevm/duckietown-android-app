package com.example.duckietownandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class DeviceAdapter(private val dataSet: MutableList<DeviceItem>,
                    private val listener: (DeviceItem) -> Unit) :
    RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val deviceName: TextView
        val statusIcon: ImageView
        val shortStatus: TextView

        init {
            deviceName = view.findViewById(R.id.deviceName)
            statusIcon = view.findViewById(R.id.deviceStatusIcon)
            shortStatus = view.findViewById(R.id.deviceShortStatus)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.device_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val item = dataSet[position]
        viewHolder.deviceName.text = item.name
        viewHolder.shortStatus.text = item.shortStatus
        viewHolder.statusIcon.setImageResource(when(item.is_online){
            true -> R.drawable.device_item_status_online
            else -> R.drawable.device_item_status_offline
        })
        viewHolder.itemView.setOnClickListener{listener(item)}
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}

