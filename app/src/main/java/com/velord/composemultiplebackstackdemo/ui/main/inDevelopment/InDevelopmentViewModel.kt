package com.velord.composemultiplebackstackdemo.ui.main.inDevelopment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velord.composemultiplebackstackdemo.R
import com.velord.composemultiplebackstackdemo.ui.navigation.NavigationData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class InDevelopmentViewModel : ViewModel() {

    val navigationEvent = MutableSharedFlow<NavigationData>()
    val backEvent = MutableSharedFlow<Unit>()

    fun onOpenNew() = viewModelScope.launch {
        navigationEvent.emit(NavigationData(R.id.toInDevelopmentFragment))
    }

    fun onBackPressed() = viewModelScope.launch {
        backEvent.emit(Unit)
    }
}