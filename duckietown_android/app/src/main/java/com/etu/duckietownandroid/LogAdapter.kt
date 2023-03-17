package com.etu.duckietownandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LogAdapter(private val dataSet: MutableList<String>) :
    RecyclerView.Adapter<LogAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val buttonPressLog: TextView

        init {
            buttonPressLog = view.findViewById(R.id.buttonPressLog)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.button_press_log_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.buttonPressLog.text = dataSet[position]
    }

    // Return the size of dataset
    override fun getItemCount() = dataSet.size

}
