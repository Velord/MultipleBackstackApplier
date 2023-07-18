package com.velord.composemultiplebackstackdemo.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import com.velord.composemultiplebackstackdemo.R
import com.velord.multiplebackstackapplier.MultipleBackstackGraphItem

enum class BottomNavigationItem(
    override val navigationGraphId: Int,
    override val startDestinationId: Int
) : MultipleBackstackGraphItem {
    Left(R.id.left_nav_graph, R.id.leftGraphFragment),
    Center(R.id.center_nav_graph, R.id.centerGraphFragment),
    Right(R.id.right_nav_graph, R.id.rightGraphFragment);


    val icon get() = when (this) {
        Left -> Icons.Outlined.ArrowBack
        Center -> Icons.Outlined.Add
        Right -> Icons.Outlined.ArrowForward
    }
}