package com.cangjie.frame.kit

interface OnClickListener<V : Any> {
    /**
     * 点击回调
     */
    fun onClick(toast: CJToast, view: V)
}