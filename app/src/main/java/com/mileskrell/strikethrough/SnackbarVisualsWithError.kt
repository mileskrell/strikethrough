package com.mileskrell.strikethrough

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

data class SnackbarVisualsWithError(
    override val message: String,
    val isError: Boolean,
) : SnackbarVisuals {
    override val actionLabel: String?
        get() = null
    override val duration: SnackbarDuration
        get() = SnackbarDuration.Short
    override val withDismissAction: Boolean
        get() = false
}
