package com.velord.composemultiplebackstackdemo.ui.main.bottomNavigation

import kotlinx.coroutines.flow.MutableStateFlow

data class BottomNavBackHandlingState(
    val isAtStartGraphDestination: Boolean = true,
    val isGrantedToProceed: Boolean = false
) {
    val isEnabled: Boolean get() = isAtStartGraphDestination && isGrantedToProceed
}

object BottomNavEventService {
    val backHandlingStateFlow = MutableStateFlow(BottomNavBackHandlingState())

    fun updateBackHandlingState(newState: BottomNavBackHandlingState) {
        backHandlingStateFlow.value =  newState
    }
}