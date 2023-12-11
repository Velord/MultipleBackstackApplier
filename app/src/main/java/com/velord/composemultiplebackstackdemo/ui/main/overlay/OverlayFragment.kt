package com.velord.composemultiplebackstackdemo.ui.main.overlay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.velord.composemultiplebackstackdemo.R
import com.velord.composemultiplebackstackdemo.ui.compose.theme.setContentWithTheme
import com.velord.composemultiplebackstackdemo.ui.utils.activityNavController
import com.velord.composemultiplebackstackdemo.ui.utils.viewLifecycleScope
import kotlinx.coroutines.launch

class OverlayFragment : Fragment() {

    private val viewModel by viewModels<OverlayViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = setContentWithTheme {
        OverlayScreen(viewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserving()
    }

    private fun initObserving() {
        viewLifecycleScope.launch {
            viewModel.navigationEvent.collect {
                activityNavController()?.navigate(it.id)
            }
        }
    }
}

@Composable
private fun OverlayScreen(viewModel: OverlayViewModel) {
    val time = remember { System.currentTimeMillis().toString() }
    Box(Modifier.fillMaxSize()) {
        Text(
            text = time,
            modifier = Modifier.align(Alignment.Center)
        )
        Button(
            onClick = viewModel::onOpenNew,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
        ) {
            Text(text = stringResource(id = R.string.open_new))
        }
    }
}

@Preview
@Composable
private fun OverlayScreenPreview() {
    OverlayScreen(OverlayViewModel())
}