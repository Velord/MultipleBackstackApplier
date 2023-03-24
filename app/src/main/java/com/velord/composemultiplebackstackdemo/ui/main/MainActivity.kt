package com.velord.composemultiplebackstackdemo.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import com.velord.composemultiplebackstackdemo.R
import com.velord.composemultiplebackstackdemo.databinding.ActivityMainBinding
import com.velord.composemultiplebackstackdemo.ui.compose.theme.MainTheme

class MainActivity : AppCompatActivity() {

    companion object {
        const val fragmentContainer = R.id.navHostFragment
    }

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setNavGraph()
    }

    private fun setNavGraph(
        @IdRes destination: Int? = null,
        bundle: Bundle? = bundleOf()
    ) {
        val navHostFragment =
            (supportFragmentManager.findFragmentById(fragmentContainer) as? NavHostFragment)
                ?: supportFragmentManager.fragments[0] as NavHostFragment

        val controller = navHostFragment.navController
        val graph = controller.navInflater.inflate(R.navigation.main_nav_graph)
        controller.graph = graph

        if (destination != null) {
            controller.navigate(destination, bundle)
        }
    }
}