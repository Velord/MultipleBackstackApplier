package com.velord.composemultiplebackstackdemo.ui.main.bottomNavigation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
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
import com.velord.multiplebackstackapplier.utils.compose.SnackBarOnBackPressHandler
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "BottomNav"

private fun Context.fireToast(text: String) {
    val description = "I am at the first at $text"
    Toast.makeText(this, description, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        show()
    }
}

internal fun Fragment.addTestCallback(
    tag: String,
    viewModel: BottomNavViewModel
) {
    requireActivity().onBackPressedDispatcher.addCallback(
        this,
        true
    ) {
        requireContext().fireToast(tag)
        isEnabled = false
        viewModel.graphCompletedHandling()
        Log.d(TAG, "onBackPressedDispatcher")
    }
}

class BottomNavFragment : Fragment(R.layout.fragment_bottom_nav) {

    private val navController by lazy {
        childFragmentManager.fragments.first().findNavController()
    }
    private val viewModel by viewModel<BottomNavViewModel>()
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

    override fun onDestroy() {
        binding = null
        lifecycle.removeObserver(multipleBackStack)
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(multipleBackStack)
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
                viewModel.finishAppEvent.collect {
                    requireActivity().finish()
                }
            }
        }
    }
}

@Composable
private fun BottomNavScreen(viewModel: BottomNavViewModel) {
    val tabFlow = viewModel.currentTabFlow.collectAsStateWithLifecycle()
    val backHandlingState = viewModel.backHandlingStateFlow.collectAsStateWithLifecycle()
    Log.d(TAG, "isBackHandlingEnabledState: ${backHandlingState.value}")

    Content(
        selectedItem = tabFlow.value,
        onClick = viewModel::onTabClick,
    )

    if (backHandlingState.value.isEnabled) {
        val str = stringResource(id = R.string.press_again_to_exit)
        SnackBarOnBackPressHandler(
            message = str,
            modifier = Modifier.padding(horizontal = 8.dp),
            enabled = backHandlingState.value.isEnabled,
            onBackClickLessThanDuration = viewModel::onBackDoubleClick,
        ) {
            Snackbar {
                Text(text = it.visuals.message)
            }
        }
    }
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
