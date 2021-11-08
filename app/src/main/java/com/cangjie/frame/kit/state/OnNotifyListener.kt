package com.cangjie.frame.kit.state


fun interface OnNotifyListener<T : MultiState> {
    fun onNotify(multiState: T)
}