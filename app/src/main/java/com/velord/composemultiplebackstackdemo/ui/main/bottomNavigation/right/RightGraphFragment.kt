package com.velord.composemultiplebackstackdemo.ui.main.bottomNavigation.right

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.velord.composemultiplebackstackdemo.R
import com.velord.composemultiplebackstackdemo.ui.compose.screen.AddNewScreen
import com.velord.composemultiplebackstackdemo.ui.compose.theme.setContentWithTheme
import com.velord.composemultiplebackstackdemo.ui.main.bottomNavigation.BottomNavViewModel
import com.velord.composemultiplebackstackdemo.ui.main.bottomNavigation.addTestCallback
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "right"

@AndroidEntryPoint
class RightGraphFragment : Fragment() {

    private val viewModel by viewModels<BottomNavViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = setContentWithTheme {
        viewModel.graphTakeResponsibility()
        addTestCallback(TAG, viewModel)

        AddNewScreen(R.string.add_new_screen_right) {
            findNavController().navigate(R.id.toInDevelopmentFragmentFromRightGraphFragment)
        }
    }
}