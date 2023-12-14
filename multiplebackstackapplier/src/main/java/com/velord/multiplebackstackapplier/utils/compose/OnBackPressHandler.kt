package com.velord.multiplebackstackapplier.utils.compose

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val DELAY_TO_EXIT_APP = 3000L
private const val DEFAULT_TIME = 0L

@Composable
fun OnBackPressHandler(
    enabled: Boolean = true,
    duration: Long = DELAY_TO_EXIT_APP,
    onEdgeViolation: () -> Unit = {},
    afterEdge: () -> Unit = {},
    onClick: suspend () -> Unit = {},
) {
    val currentOnEdgeViolation by rememberUpdatedState(onEdgeViolation)
    val currentAfterEdge by rememberUpdatedState(afterEdge)
    val currentOnClick by rememberUpdatedState(onClick)

    val timeSnapshotState = remember {
        mutableLongStateOf(DEFAULT_TIME)
    }
    val triggerState = remember {
        mutableLongStateOf(DEFAULT_TIME)
    }

    BackHandler(enabled) {
        triggerState.longValue = System.currentTimeMillis()
    }

    LaunchedEffect(key1 = triggerState.longValue) {
        if (triggerState.longValue == DEFAULT_TIME) return@LaunchedEffect

        val isEdgeViolated = (timeSnapshotState.longValue + duration) > System.currentTimeMillis()
        if (isEdgeViolated) currentOnEdgeViolation()

        launch {
            currentOnClick()
        }
        timeSnapshotState.longValue = System.currentTimeMillis()
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
    val snackBarHostState = remember {
        mutableStateOf(SnackbarHostState())
    }

    OnBackPressHandler(
        enabled = enabled,
        duration = duration,
        onEdgeViolation = onBackClickLessThanDuration,
        afterEdge = {
            snackBarHostState.value.currentSnackbarData?.dismiss()
        },
        onClick = {
            snackBarHostState.value.showSnackbar(
                message = message,
                duration = SnackbarDuration.Indefinite,
            )
        }
    )

    SnackbarHost(
        hostState = snackBarHostState.value,
        modifier = modifier,
        snackbar = content
    )
}