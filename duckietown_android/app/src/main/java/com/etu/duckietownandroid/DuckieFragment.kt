package com.etu.duckietownandroid

import android.os.Bundle
import android.view.Menu
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

open class DuckieFragment(
    @StringRes val how_to_use_id: Int? = null,
    @DrawableRes val image_id: Int? = null
): Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        how_to_use_id?.let{ setHasOptionsMenu(true) }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(how_to_use_id == null) return
        val item = menu.findItem(R.id.menu_how_to_use)
        item.isVisible = true
        item.setOnMenuItemClickListener {
            DialogInfoErrorFragment(
                getString(how_to_use_id),
                getString(R.string.dialog_title_info),
                image_id
            ).show(
                activity?.supportFragmentManager!!,
                "info_error"
            )
            true
        }
    }

    fun safeNavigation(@IdRes id: Int, args: Bundle? = null) {
        val nav = findNavController()
        nav.currentDestination?.getAction(id)?.run { nav.navigate(id, args) }
    }

}