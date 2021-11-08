package com.cangjie.frame.kit.state

import android.app.Activity
import android.view.View

fun View.bindMultiState() = MultiStatePage.bindMultiState(this)

fun Activity.bindMultiState() = MultiStatePage.bindMultiState(this)