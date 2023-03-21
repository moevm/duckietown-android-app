package com.etu.duckietownandroid

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.etu.duckietownandroid.databinding.FragmentMapBinding

class MapFragment : DuckieFragment(R.string.how_to_use_map) {

    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapClearPoints.setOnClickListener {
            binding.mapImage.clearPoints()
        }

        binding.mapSendAction.setOnClickListener {
            val fp = binding.mapImage.firstPoint
            val sp = binding.mapImage.secondPoint
            if(fp != null && sp != null){
                copyPointsToClipboard(fp, sp)
                performAction(fp, sp)
            }else{
                Toast.makeText(activity, "Set two points on map", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun performAction(p1: Pair<Float, Float>, p2: Pair<Float, Float>){
        Toast.makeText(activity, "Current points are $p1 and $p2\nPoints coordinates are copied to clipboard", Toast.LENGTH_LONG).show()
    }

    private fun copyPointsToClipboard(p1: Pair<Float, Float>, p2: Pair<Float, Float>) {
        val textToCopy = "$p1 $p2"

        val clipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}