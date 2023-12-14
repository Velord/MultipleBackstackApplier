package com.velord.composemultiplebackstackdemo.ui.main.bottomNavigation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.velord.composemultiplebackstackdemo.R
import com.velord.composemultiplebackstackdemo.databinding.FragmentBottomNavBinding
import com.velord.composemultiplebackstackdemo.ui.compose.theme.setContentWithTheme
import com.velord.composemultiplebackstackdemo.ui.navigation.BottomNavigationItem
import com.velord.composemultiplebackstackdemo.ui.utils.viewLifecycleScope
import com.velord.multiplebackstackapplier.MultipleBackstack
import kotlinx.coroutines.launch

internal const val TAG = "!DEMO"

internal fun Context.fireToast(text: String) {
    val description = "I am at the first at $text"
    Toast.makeText(this, description, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        show()
    }
}

class BottomNavFragment : Fragment(R.layout.fragment_bottom_nav) {

    private val navController by lazy {
        childFragmentManager.fragments.first().findNavController()
    }
    private val viewModel by viewModels<BottomNavViewModel>()
    private var binding: FragmentBottomNavBinding? = null

    private val multipleBackStack by lazy {
        MultipleBackstack(
            navController = lazy { navController },
            lifecycleOwner = this,
            flowOnSelect = viewModel.currentTabFlow,
            context = requireContext(),
            items = viewModel.getNavigationItems(),
            onMenuChange = {
                val current = navController.currentDestination
                viewModel.updateBackHandling(current)
            }
        )
    }

    private var backkPressCallback: OnBackPressedCallback? = null

    override fun onDestroy() {
        binding = null
        lifecycle.removeObserver(multipleBackStack)
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(multipleBackStack)

        Log.d(TAG, "onCreate")
//        backkPressCallback = requireActivity().onBackPressedDispatcher.addCallback(
//            this,
//            true
//        ) {
//            requireContext().fireToast("bottom nav")
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBottomNavBinding.bind(view).apply {
            initView()
        }
        initObserving()
    }

    context(FragmentBottomNavBinding)
    private fun initView() {
        bottomNavBarView.setContentWithTheme {
            BottomNavScreen(viewModel)
        }
    }

    private fun initObserving() {
        viewLifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.finishAppEvent.collect {
                        requireActivity().finish()
                    }
                }
                launch {
//                    viewModel.isBackHandlingEnabledFlow.collect {
//                        Log.d(TAG, "isBackHandlingEnabledState from initObserving: $it")
//                        backkPressCallback?.isEnabled = it
//                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavScreen(viewModel: BottomNavViewModel) {
    val tabFlow = viewModel.currentTabFlow.collectAsStateWithLifecycle()
    val isBackHandlingEnabledState =
        viewModel.isBackHandlingEnabledFlow.collectAsStateWithLifecycle()
    //Log.d(TAG, "isBackHandlingEnabledState: ${isBackHandlingEnabledState.value}")

    Content(
        selectedItem = tabFlow.value,
        onClick = viewModel::onTabClick,
    )


    // You might think that my code is buggy so I commented it.
    // Instead of I added BackHandler from androidx.activity.compose with simple Toast
    if (isBackHandlingEnabledState.value) {
        val context = LocalContext.current
        BackHandler {
            val description = "I am at the first destination of the graph"
            Toast.makeText(context, description, Toast.LENGTH_SHORT).apply {
                setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                show()
            }
        }
    }

    /*
        SnackBarOnBackPressHandler is prepared for testing with
        BackHandler from androidx.activity.compose
        just uncomment it to see yourself it doesn't work
     */
//    val str = stringResource(id = R.string.press_again_to_exit)
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
        BottomNavigationItem.entries.forEach {
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

@Preview
@Composable
private fun BottomNavContentPreview() {
    Content(
        selectedItem = BottomNavigationItem.Left,
        onClick = {},
    )
}
