package com.cangjie.frame.kit.state.state

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.cangjie.frame.kit.state.MultiState
import com.cangjie.frame.kit.state.MultiStateContainer

class SuccessState : MultiState() {
    override fun onCreateMultiStateView(
        context: Context,
        inflater: LayoutInflater,
        container: MultiStateContainer
    ): View {
        return View(context)
    }

    override fun onMultiStateViewCreate(view: View) = Unit

}