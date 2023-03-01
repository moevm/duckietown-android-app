package com.example.duckietownandroid

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.duckietownandroid.databinding.FragmentMainBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listButtonListener = { it: View ->
            val listType = when(it.id){
                binding.autobotsButton.id -> "autobots"
                binding.watchtowersButton.id -> "watchtowers"
                binding.camerasButton.id -> "cameras"
                else -> "unknown"
            }
            val bundle = bundleOf("list_type" to listType)
            findNavController().navigate(R.id.action_MainFragment_to_ListFragment, bundle)
        }
        binding.autobotsButton.setOnClickListener(listButtonListener)
        binding.watchtowersButton.setOnClickListener(listButtonListener)
        binding.camerasButton.setOnClickListener(listButtonListener)
        binding.mapButton.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_MapFragment)
        }
        binding.arucoButton.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_arUcoFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}