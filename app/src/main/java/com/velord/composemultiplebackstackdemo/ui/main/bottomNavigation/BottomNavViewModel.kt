package com.velord.composemultiplebackstackdemo.ui.main.bottomNavigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination
import com.velord.composemultiplebackstackdemo.ui.navigation.BottomNavigationItem
import com.velord.multiplebackstackapplier.utils.isCurrentStartDestination
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BottomNavViewModel : ViewModel() {

    val currentTabFlow = MutableStateFlow(BottomNavigationItem.Left)
    val isBackHandlingEnabledFlow = MutableStateFlow(false)
    val finishAppEvent: MutableSharedFlow<Unit> = MutableSharedFlow()

    fun getNavigationItems() = BottomNavigationItem.values().toList()

    fun onTabClick(newTab: BottomNavigationItem) {
        if (newTab == currentTabFlow.value) return
        currentTabFlow.value = newTab
    }

    fun onBackDoubleClick() = viewModelScope.launch {
        finishAppEvent.emit(Unit)
    }

    fun updateBackHandling(currentNavigationDestination: NavDestination?) {
        val isStart = currentNavigationDestination.isCurrentStartDestination(getNavigationItems())
        isBackHandlingEnabledFlow.value = isStart
    }
}