package com.etu.duckietownandroid

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.etu.duckietownandroid.databinding.FragmentMainBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainFragment : DuckieFragment() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
            safeNavigation(R.id.action_MainFragment_to_ListFragment, bundle)
        }
        binding.autobotsButton.setOnClickListener(listButtonListener)
        binding.watchtowersButton.setOnClickListener(listButtonListener)
        binding.camerasButton.setOnClickListener(listButtonListener)
        binding.mapButton.setOnClickListener {
            safeNavigation(R.id.action_MainFragment_to_MapFragment)
        }
        binding.arucoButton.setOnClickListener {
            safeNavigation(R.id.action_MainFragment_to_arUcoFragment)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.menu_item_test)
        item.isVisible = true
        item.setOnMenuItemClickListener {
            val intent = Intent(activity, SettingsActivity::class.java)
            startActivity(intent)
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}