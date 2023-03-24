package com.velord.multiplebackstackapplier

import androidx.navigation.NavDestination

fun NavDestination?.isCurrentStartDestination(
    items: List<MultipleBackstackGraphItem>,
): Boolean = items
    .map { it.startDestinationId }
    .contains(this?.id)