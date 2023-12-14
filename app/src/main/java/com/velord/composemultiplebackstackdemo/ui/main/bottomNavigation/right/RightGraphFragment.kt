package com.velord.composemultiplebackstackdemo.ui.main.bottomNavigation.right

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.velord.composemultiplebackstackdemo.R
import com.velord.composemultiplebackstackdemo.ui.compose.screen.AddNewScreen
import com.velord.composemultiplebackstackdemo.ui.compose.theme.setContentWithTheme
import com.velord.composemultiplebackstackdemo.ui.main.bottomNavigation.BottomNavViewModel
import com.velord.composemultiplebackstackdemo.ui.main.bottomNavigation.TAG
import com.velord.composemultiplebackstackdemo.ui.main.bottomNavigation.fireToast

class RightGraphFragment : Fragment() {

    private val viewModel by navGraphViewModels<BottomNavViewModel>(R.id.bottom_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = setContentWithTheme {
        AddNewScreen(R.string.add_new_screen_right) {
            findNavController().navigate(R.id.toInDevelopmentFragment)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            true
        ) {
            requireContext().fireToast("right")
            viewModel.isBackHandlingEnabledFlow
            Log.d(TAG, "onBackPressedDispatcher")
        }
    }
}