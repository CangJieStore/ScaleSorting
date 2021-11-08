package com.cangjie.frame.kit.state

import android.content.Context
import android.view.LayoutInflater
import android.view.View

abstract class MultiState {

    /**
     * 创建stateView
     */
    abstract fun onCreateMultiStateView(
        context: Context,
        inflater: LayoutInflater,
        container: MultiStateContainer
    ): View

    /**
     * stateView创建完成
     */
    abstract fun onMultiStateViewCreate(view: View)
}