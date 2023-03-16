package com.etu.duckietownandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AutobotStatusItemAdapter(
    private val fullStatus: MutableMap<StatusKeys, DeviceStatus>,
    private val arrKeys: Array<StatusKeys> = StatusKeys.values()) :
    RecyclerView.Adapter<AutobotStatusItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            // Define click listener for the ViewHolder's View
            textView = view.findViewById(R.id.autobot_status_item_text)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.autobot_status_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        fullStatus[arrKeys[position]]?.let {
            viewHolder.textView.text = it.getStingStatus()
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = arrKeys.size

}
