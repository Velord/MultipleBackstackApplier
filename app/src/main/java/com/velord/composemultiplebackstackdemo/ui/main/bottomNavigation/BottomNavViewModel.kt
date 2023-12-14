package com.velord.composemultiplebackstackdemo.ui.main.bottomNavigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination
import com.velord.composemultiplebackstackdemo.ui.navigation.BottomNavigationItem
import com.velord.multiplebackstackapplier.utils.isCurrentStartDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomNavViewModel @Inject constructor(
    private val bottomNavEventService: BottomNavEventService
): ViewModel() {

    val currentTabFlow = MutableStateFlow(BottomNavigationItem.Left)
    val backHandlingStateFlow = bottomNavEventService.backHandlingStateFlow
    val finishAppEvent: MutableSharedFlow<Unit> = MutableSharedFlow()

    fun getNavigationItems() = BottomNavigationItem.entries

    fun onTabClick(newTab: BottomNavigationItem) {
        if (newTab == currentTabFlow.value) return
        currentTabFlow.value = newTab
    }

    fun onBackDoubleClick() = viewModelScope.launch {
        finishAppEvent.emit(Unit)
    }

    fun updateBackHandling(currentNavigationDestination: NavDestination?) {
        val isStart = currentNavigationDestination.isCurrentStartDestination(getNavigationItems())
        val newState = backHandlingStateFlow.value.copy(isAtStartGraphDestination = isStart)
        bottomNavEventService.updateBackHandlingState(newState)
    }

    private fun changeGrantedToProceed(isGranted: Boolean) {
        val newState = backHandlingStateFlow.value.copy(isGrantedToProceed = isGranted)
        bottomNavEventService.updateBackHandlingState(newState)
    }

    fun graphCompletedHandling() {
        changeGrantedToProceed(true)
    }

    fun graphTakeResponsibility() {
        changeGrantedToProceed(false)
    }
}