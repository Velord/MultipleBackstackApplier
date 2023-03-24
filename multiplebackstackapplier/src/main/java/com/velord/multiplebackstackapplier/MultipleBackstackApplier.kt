package com.velord.multiplebackstackapplier

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.annotation.IdRes
import androidx.core.view.forEach
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationBarMenu
import com.velord.multiplebackstackapplier.MultipleBackstackApplier.matchDestination
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

    fun createListener(
        context: Context,
        items: List<MultipleBackstackGraphItem>,
        onChangeDestination: (MenuItem) -> Unit
    ): NavController.OnDestinationChangedListener = NavController.OnDestinationChangedListener {
            _, destination, _ ->
        createNavigationBarMenu(context, items).forEach { item ->
            if (destination.matchDestination(item.itemId)) {
                item.isChecked = true
                onChangeDestination(item)
            }
        }
    }

    // Copy from androidx.navigation.ui.NavigationUI. Cause it's internal.
    internal fun NavDestination.matchDestination(@IdRes destId: Int): Boolean =
        hierarchy.any { it.id == destId }
}

class MultipleBackstack(
    private val navController: Lazy<NavController>,
    private val lifecycleOwner: LifecycleOwner,
    private val context: Context,
    private val items: List<MultipleBackstackGraphItem>,
    private val flowOnSelect: Flow<MultipleBackstackGraphItem>,
    onMenuChange: (MenuItem) -> Unit,
) : DefaultLifecycleObserver {

    private var listener: NavController.OnDestinationChangedListener =
        MultipleBackstackApplier.createListener(context, items, onMenuChange)

    init {
        observe()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        navController.value.removeOnDestinationChangedListener(listener)
    }

    override fun onResume(owner: LifecycleOwner) {
        navController.value.addOnDestinationChangedListener(listener)
        super.onResume(owner)
    }

    private fun observe() {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flowOnSelect.collectLatest { navItem ->
                    Log.d("@@@", "onSelect: ${navItem.navigationGraphId}")
                    val menu = MultipleBackstackApplier.createNavigationBarMenu(context, items)
                    val menuItem = menu.findItem(navItem.navigationGraphId)
                    NavigationUI.onNavDestinationSelected(
                        item = menuItem,
                        navController = navController.value
                    )
                }
            }
        }
    }
}