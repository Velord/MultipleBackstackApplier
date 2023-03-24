package com.velord.multiplebackstackapplier

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.annotation.IdRes
import androidx.core.view.forEach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationBarMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

object MultipleBackstackApplier {

    private const val GROUP = 0
    private const val CATEGORY_ORDER = 0

    // Create "fake" NavigationBarMenu. Then summon MenuItems to it.
    // Ids of MenuItems must be the same as navigation graph ids.
    @SuppressLint("RestrictedApi")
    fun createNavigationBarMenu(
        context: Context,
        items: List<MultipleBackstackGraphItem>
    ): NavigationBarMenu = NavigationBarMenu(
        context, this.javaClass, items.size
    ).apply {
        items.forEach {
            add(GROUP, it.navigationGraphId, CATEGORY_ORDER, null)
        }
    }

    @SuppressLint("RestrictedApi")
    fun setupWithNavController(
        items: List<MultipleBackstackGraphItem>,
        navigationView: View,
        navController: NavController,
        flowOnSelect: Flow<MultipleBackstackGraphItem>,
        onMenuChange: (MenuItem) -> Unit,
    ) {
        val menu = createNavigationBarMenu(navigationView.context, items)

        navigationView.findViewTreeLifecycleOwner()?.let {
            it.lifecycleScope.launch {
                it.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    flowOnSelect.collectLatest { navItem ->
                        val menuItem = menu.findItem(navItem.navigationGraphId)
                        NavigationUI.onNavDestinationSelected(
                            item = menuItem,
                            navController = navController
                        )
                    }
                }
            }
        }
        val weakReference = WeakReference(navigationView)
        navController.addOnDestinationChangedListener(
            object : NavController.OnDestinationChangedListener {
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?
                ) {
                    val view = weakReference.get()
                    if (view == null) {
                        navController.removeOnDestinationChangedListener(this)
                        return
                    }
                    menu.forEach { item ->
                        if (destination.matchDestination(item.itemId)) {
                            item.isChecked = true
                            onMenuChange(item)
                        }
                    }
                }
            }
        )
    }

    // Copy from androidx.navigation.ui.NavigationUI. Cause it's internal.
    fun NavDestination.matchDestination(@IdRes destId: Int): Boolean =
        hierarchy.any { it.id == destId }
}