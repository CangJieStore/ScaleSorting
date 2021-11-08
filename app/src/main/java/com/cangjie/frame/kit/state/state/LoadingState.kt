package com.cangjie.frame.kit.state.state

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import cangjie.scale.sorting.R
import com.cangjie.frame.kit.state.MultiState
import com.cangjie.frame.kit.state.MultiStateContainer
import com.cangjie.frame.kit.state.MultiStatePage

class LoadingState : MultiState() {
    private lateinit var tvLoadingMsg: TextView
    override fun onCreateMultiStateView(
        context: Context,
        inflater: LayoutInflater,
        container: MultiStateContainer
    ): View {
        return inflater.inflate(R.layout.mult_state_loading, container, false)
    }

    override fun onMultiStateViewCreate(view: View) {
        tvLoadingMsg = view.findViewById(R.id.tv_loading_msg)
        setLoadingMsg(MultiStatePage.config.loadingMsg)
    }

    fun setLoadingMsg(loadingMsg: String) {
        tvLoadingMsg.text = loadingMsg
    }
}