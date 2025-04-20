package com.mileskrell.strikethrough

import android.content.ClipData
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mileskrell.strikethrough.ui.theme.StrikethroughTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StrikethroughTheme {
                MainActivityContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainActivityContent() {
    var input by rememberSaveable { mutableStateOf("") }
    val processedText = strikeText(input)
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.app_name)) })
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        floatingActionButton = {
            if (input.isNotEmpty()) {
                val context = LocalContext.current
                val clipboardManager = LocalClipboardManager.current
                val scope = rememberCoroutineScope()
                FloatingActionButton(
                    onClick = {
                        val clip = ClipData.newPlainText(context.getString(R.string.clip_data_label), processedText)
                        clipboardManager.setClip(ClipEntry(clip))
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                            scope.launch {
                                snackbarHostState.showSnackbar(context.getString(R.string.copied_to_clipboard_confirmation))
                            }
                        }
                    },
                    modifier = Modifier.imePadding(),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_content_copy_24),
                        tint = null,
                        contentDescription = stringResource(R.string.copy_to_clipboard_content_description),
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()).padding(innerPadding).padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(Unit) { focusRequester.requestFocus() }
            TextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.focusRequester(focusRequester),
                placeholder = { Text(stringResource(R.string.text_field_placeholder)) },
            )
            Text(
                text = processedText,
                modifier = Modifier.padding(vertical = 32.dp),
            )
            Text(
                text = stringResource(R.string.use_context_menu_suggestion),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainActivityPreview() {
    StrikethroughTheme {
        MainActivityContent()
    }
}
