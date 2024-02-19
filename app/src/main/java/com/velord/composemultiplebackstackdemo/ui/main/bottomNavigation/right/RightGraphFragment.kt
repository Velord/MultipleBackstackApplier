package com.velord.composemultiplebackstackdemo.ui.main.bottomNavigation.right

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.velord.composemultiplebackstackdemo.R
import com.velord.composemultiplebackstackdemo.ui.compose.screen.AddNewScreen
import com.velord.composemultiplebackstackdemo.ui.compose.theme.setContentWithTheme
import com.velord.composemultiplebackstackdemo.ui.main.bottomNavigation.BottomNavViewModel
import com.velord.composemultiplebackstackdemo.ui.main.bottomNavigation.addTestCallback
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "right"

class RightGraphFragment : Fragment() {

    private val viewModel by viewModel<BottomNavViewModel>()

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