package com.velord.multiplebackstackapplier.utils.compose

import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.velord.multiplebackstackapplier.TAG
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val DELAY_TO_EXIT_APP = 3000L
private const val DEFAULT_TIME = 0L

@Composable
fun MyBackHandler(enabled: Boolean = true, onBack: () -> Unit) {
    Log.d(TAG, "MyBackHandler")
    // Safely update the current `onBack` lambda when a new one is provided
    val currentOnBack by rememberUpdatedState(onBack)
    // Remember in Composition a back callback that calls the `onBack` lambda
    val backCallback = object : OnBackPressedCallback(enabled) {
        override fun handleOnBackPressed() {
            currentOnBack()
        }
    }
    // On every successful composition, update the callback with the `enabled` value
    SideEffect {
        backCallback.isEnabled = enabled
    }
    val backDispatcher = checkNotNull(LocalOnBackPressedDispatcherOwner.current) {
        "No OnBackPressedDispatcherOwner was provided via LocalOnBackPressedDispatcherOwner"
    }.onBackPressedDispatcher
    val lifecycleOwner = LocalLifecycleOwner.current
    // "enabled" is a key which guarantees
    // addCallback will be triggered when user leaves composition and return back a.k.a resubscribing
    DisposableEffect(lifecycleOwner, backDispatcher, enabled) {
        // Add callback to the backDispatcher
        Log.d(TAG, "addCallback")
        backDispatcher.addCallback(lifecycleOwner, backCallback)
        // When the effect leaves the Composition, remove the callback
        onDispose {
            backCallback.remove()
        }
    }
}

@Composable
fun OnBackPressHandler(
    enabled: Boolean = true,
    duration: Long = DELAY_TO_EXIT_APP,
    onEdgeViolation: () -> Unit = {},
    afterEdge: () -> Unit = {},
    onClick: suspend () -> Unit = {},
) {
    Log.d(TAG, "OnBackPressHandler")
    val currentOnEdgeViolation by rememberUpdatedState(onEdgeViolation)
    val currentAfterEdge by rememberUpdatedState(afterEdge)
    val currentOnClick by rememberUpdatedState(onClick)

    val timeShapshotState = remember {
        mutableStateOf(DEFAULT_TIME)
    }
    val triggerState = remember {
        mutableStateOf(DEFAULT_TIME)
    }

    MyBackHandler(enabled) {
        triggerState.value = System.currentTimeMillis()
    }

    LaunchedEffect(key1 = triggerState.value) {
        if (triggerState.value == DEFAULT_TIME) return@LaunchedEffect

        val isEdgeViolated = (timeShapshotState.value + duration) > System.currentTimeMillis()
        if (isEdgeViolated) currentOnEdgeViolation()

        launch {
            currentOnClick()
        }
        timeShapshotState.value = System.currentTimeMillis()
        delay(duration)
        currentAfterEdge()
    }
}

@Composable
fun SnackBarOnBackPressHandler(
    message: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    duration: Long = DELAY_TO_EXIT_APP,
    onBackClickLessThanDuration: () -> Unit = {},
    content: @Composable (SnackbarData) -> Unit,
) {
    val snackbarHostState = remember {
        mutableStateOf(SnackbarHostState())
    }

    OnBackPressHandler(
        enabled = enabled,
        duration = duration,
        onEdgeViolation = onBackClickLessThanDuration,
        afterEdge = {
            snackbarHostState.value.currentSnackbarData?.dismiss()
        },
        onClick = {
            snackbarHostState.value.showSnackbar(
                message = message,
                duration = SnackbarDuration.Indefinite,
            )
        }
    )

    SnackbarHost(
        hostState = snackbarHostState.value,
        modifier = modifier,
        snackbar = content
    )
}