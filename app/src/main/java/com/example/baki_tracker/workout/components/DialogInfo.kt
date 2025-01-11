package com.example.baki_tracker.workout.components

import androidx.annotation.StringRes

data class DialogInfo(
    @StringRes val title: Int,
    @StringRes val description: Int,
    @StringRes val confirmButtonLabel: Int,
    @StringRes val dismissButtonLabel: Int,
    val onConfirm: () -> Unit,
    val onDismiss: () -> Unit
)
