package com.example.baki_tracker.workout.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun CustomConfirmationDialog(
    title: String,
    description: String,
    confirmButtonLabel: String,
    dismissButtonLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(confirmButtonLabel, color = MaterialTheme.colorScheme.error)
            }
        }, dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissButtonLabel)
            }
        }, title = { Text(text = "$title ?") }, text = { Text(text = description) })
}