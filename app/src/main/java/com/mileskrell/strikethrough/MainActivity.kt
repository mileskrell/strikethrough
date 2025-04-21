package com.mileskrell.strikethrough

import android.annotation.SuppressLint
import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mileskrell.strikethrough.ui.theme.StrikethroughTheme
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

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

@SuppressLint("WrongConstant")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainActivityContent() {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.main_activity_title)) })
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                val isError = (data.visuals as? SnackbarVisualsWithError)?.isError ?: false
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isError) MaterialTheme.colorScheme.errorContainer else SnackbarDefaults.color,
                    contentColor = if (isError) MaterialTheme.colorScheme.error else SnackbarDefaults.contentColor,
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()).padding(innerPadding).padding(horizontal = 16.dp),
        ) {
            Text(
                text = stringResource(R.string.how_to_use_this_app_title),
            )
            Text(
                text = stringResource(R.string.context_menu_instructions),
                modifier = Modifier.padding(top = 32.dp),
            )

            val isDarkTheme = isSystemInDarkTheme()
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.quick_settings_instructions_1))
                    withLink(
                        LinkAnnotation.Url(
                            url = stringResource(R.string.google_support_quick_settings_url),
                            styles = TextLinkStyles(SpanStyle(color = if (isDarkTheme) Color.Cyan else Color.Blue)),
                        ),
                    ) {
                        append(stringResource(R.string.quick_settings_instructions_2))
                    }
                    append(stringResource(if (canShowAddTileRequest) R.string.quick_settings_instructions_3_can_show_add_tile_request else R.string.quick_settings_instructions_3_cannot_show_add_tile_request))
                },
                modifier = Modifier.padding(top = 32.dp),
            )

            if (canShowAddTileRequest) {
                val context = LocalContext.current
                val scope = rememberCoroutineScope()
                Button(
                    onClick = {
                        val statusBarManager = context.getSystemService(Context.STATUS_BAR_SERVICE) as StatusBarManager
                        statusBarManager.requestAddTileService(
                            ComponentName(context, StrikethroughTileService::class.java),
                            context.getString(R.string.app_name),
                            Icon.createWithResource(context, R.drawable.outline_format_strikethrough_24),
                            Executors.newSingleThreadExecutor(),
                        ) { resultCode ->
                            val toastMessageRes = when (resultCode) {
                                StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED -> R.string.tile_add_request_result_tile_added
                                StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED -> R.string.tile_add_request_result_tile_already_added
                                else -> R.string.tile_add_request_result_tile_not_added
                            }
                            val isError = toastMessageRes == R.string.tile_add_request_result_tile_not_added
                            val visuals = SnackbarVisualsWithError(context.getString(toastMessageRes), isError)
                            scope.launch { snackbarHostState.showSnackbar(visuals) }
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 32.dp),
                ) {
                    Text(stringResource(R.string.add_quick_settings_tile_button_label))
                }
            }
        }
    }
}

private val canShowAddTileRequest = Build.VERSION.SDK_INT >= 33

@Preview(showBackground = true)
@Composable
private fun MainActivityPreview() {
    StrikethroughTheme {
        MainActivityContent()
    }
}
