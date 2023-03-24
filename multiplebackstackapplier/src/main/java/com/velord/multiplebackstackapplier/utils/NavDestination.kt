package com.velord.multiplebackstackapplier.utils

import androidx.navigation.NavDestination
import com.velord.multiplebackstackapplier.MultipleBackstackGraphItem

fun NavDestination?.isCurrentStartDestination(
    items: List<MultipleBackstackGraphItem>,
): Boolean = items
    .map { it.startDestinationId }
    .contains(this?.id)