package com.velord.composemultiplebackstackdemo.ui.main.bottomNavigation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.velord.composemultiplebackstackdemo.R
import com.velord.composemultiplebackstackdemo.databinding.FragmentBottomNavBinding
import com.velord.composemultiplebackstackdemo.ui.compose.theme.MainTheme
import com.velord.composemultiplebackstackdemo.ui.compose.theme.setContentWithTheme
import com.velord.composemultiplebackstackdemo.ui.navigation.BottomNavigationItem
import com.velord.composemultiplebackstackdemo.ui.utils.viewLifecycleScope
import com.velord.multiplebackstackapplier.MultipleBackstackApplier
import com.velord.multiplebackstackapplier.MultipleBackstackApplier.matchDestination
import com.velord.multiplebackstackapplier.utils.compose.SnackBarOnBackPressHandler
import kotlinx.coroutines.launch

class BottomNavFragment : Fragment(R.layout.fragment_bottom_nav) {

    private val navController by lazy {
        childFragmentManager.fragments.first().findNavController()
    }
    private val viewModel by viewModels<BottomNavViewModel>()
    private var binding: FragmentBottomNavBinding? = null

    private var listener: NavController.OnDestinationChangedListener =
        NavController.OnDestinationChangedListener { controller, destination, _ ->
            MultipleBackstackApplier.createNavigationBarMenu(
                requireContext(),
                viewModel.getNavigationItems()
            ).forEach { item ->
                if (destination.matchDestination(item.itemId)) {
                    item.isChecked = true
                    val current = controller.currentDestination
                    viewModel.updateBackHandling(current)
                }
            }
        }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBottomNavBinding.bind(view).apply {
            initView()
            initMultipleBackStack()
        }
        initObserving()
    }

    context(FragmentBottomNavBinding)
    private fun initView() {
        bottomNavBarView.setContentWithTheme {
            BottomNavScreen(viewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        listener?.let {
            Log.d("@@@", "onResume")
            navController.addOnDestinationChangedListener(it)
        }
    }

    override fun onPause() {
        listener?.let {
            Log.d("@@@", "onPause")
            navController.removeOnDestinationChangedListener(it)
        }
        super.onPause()
    }

    context(FragmentBottomNavBinding)
    private fun initMultipleBackStack() {
//        Log.d("@@@", "initMultipleBackStack")
//        if (listener == null) {
//            listener = MultipleBackstackApplier.setupWithNavController(
//                items = viewModel.getNavigationItems(),
//                navigationView = bottomNavBarView,
//                lifecycleOwner = this@BottomNavFragment,
//                navController = navController,
//                flowOnSelect = viewModel.currentTabFlow,
//            ) {
//                val destination = navController.currentDestination
//                viewModel.updateBackHandling(destination)
//            }
//        }
    }

    private fun initObserving() {
        viewLifecycleScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.finishAppEvent.collect {
                        requireActivity().finish()
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.currentTabFlow.collect {
                        Log.d("@@@", "currentTabFlow.collect: $it")
                        val menu = MultipleBackstackApplier.createNavigationBarMenu(
                            requireContext(),
                            viewModel.getNavigationItems()
                        )
                        val menuItem = menu.findItem(it.navigationGraphId)
                        NavigationUI.onNavDestinationSelected(
                            item = menuItem,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavScreen(viewModel: BottomNavViewModel) {
    val tabFlow = viewModel.currentTabFlow.collectAsStateWithLifecycle()
    val isBackHandlingEnabledState = viewModel.isBackHandlingEnabledFlow.collectAsStateWithLifecycle()

    Content(
        selectedItem = tabFlow.value,
        onClick = viewModel::onTabClick,
    )

    val str = stringResource(id = R.string.press_again_to_exit)
    BackHandler(isBackHandlingEnabledState.value) {
        Log.d("@@@", "BackHandler")
    }
//    SnackBarOnBackPressHandler(
//        message = str,
//        modifier = Modifier.padding(horizontal = 8.dp),
//        enabled = isBackHandlingEnabledState.value,
//        onBackClickLessThanDuration = viewModel::onBackDoubleClick,
//    ) {
//        Snackbar {
//            Text(text = it.visuals.message)
//        }
//    }
}

@Composable
private fun Content(
    selectedItem: BottomNavigationItem,
    onClick: (BottomNavigationItem) -> Unit,
) {
    NavigationBar(
        modifier = Modifier
            .navigationBarsPadding()
            .height(72.dp),
    ) {
        BottomNavigationItem.values().forEach {
            val isSelected = selectedItem == it
            NavigationBarItem(
                selected = isSelected,
                onClick = { onClick(it) },
                label = {},
                icon = {
                    val color = MaterialTheme.colorScheme.run {
                        if (isSelected) Color.Red else onSurface
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier
                                .scale(if (isSelected) 1.5f else 1f)
                                .size(28.dp)
                        )
                        Text(
                            text = it.name,
                            modifier = Modifier.padding(top = 4.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                        LocalAbsoluteTonalElevation.current
                    )
                )
            )
        }
    }
}

@Composable
private fun BottomNavContentPreview() {
    Content(
        selectedItem = BottomNavigationItem.Left,
        onClick = {},
    )
}
