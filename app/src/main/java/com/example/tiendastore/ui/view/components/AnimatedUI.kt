package com.example.tiendastore.ui.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale

@Composable
fun AnimatedPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed = interaction.collectIsPressedAsState().value
    val scale = if (pressed) 0.98f else 1f
    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interaction,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
    ) { Text(text) }
}

@Composable
fun AnimatedOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed = interaction.collectIsPressedAsState().value
    val scale = if (pressed) 0.98f else 1f
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interaction,
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
    ) { Text(text) }
}

@Composable
fun AnimatedAlert(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: @Composable (() -> Unit),
    text: @Composable (() -> Unit),
    confirm: @Composable (() -> Unit),
    dismiss: @Composable (() -> Unit)? = null
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = title,
            text = text,
            confirmButton = confirm,
            dismissButton = dismiss
        )
    }
}

