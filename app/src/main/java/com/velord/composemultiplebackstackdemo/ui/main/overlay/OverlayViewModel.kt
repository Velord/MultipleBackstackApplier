package com.velord.composemultiplebackstackdemo.ui.main.overlay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velord.composemultiplebackstackdemo.R
import com.velord.composemultiplebackstackdemo.ui.navigation.NavigationData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class OverlayViewModel : ViewModel() {

    val navigationEvent = MutableSharedFlow<NavigationData>()

    fun onOpenNew() = viewModelScope.launch {
        navigationEvent.emit(NavigationData(R.id.toInDevelopmentFragment))
    }
}