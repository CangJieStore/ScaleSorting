package com.cangjie.frame.kit.bottom

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentManager

class BottomDialog : BaseBottomDialog() {
    private var mFragmentManager: FragmentManager? = null
    override var cancelOutside = super.cancelOutside
        private set
    override var fragmentTag = super.fragmentTag
        private set
    override var dimAmount = super.dimAmount
        private set
    override var height = super.height
        private set

    @LayoutRes
    override var layoutRes = 0
        private set
    private var mViewListener: ViewListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            layoutRes = savedInstanceState.getInt(KEY_LAYOUT_RES)
            height = savedInstanceState.getInt(KEY_HEIGHT)
            dimAmount = savedInstanceState.getFloat(KEY_DIM)
            cancelOutside = savedInstanceState.getBoolean(KEY_CANCEL_OUTSIDE)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_LAYOUT_RES, layoutRes)
        outState.putInt(KEY_HEIGHT, height)
        outState.putFloat(KEY_DIM, dimAmount)
        outState.putBoolean(KEY_CANCEL_OUTSIDE, cancelOutside)
        super.onSaveInstanceState(outState)
    }

    override fun bindView(v: View?) {
        if (mViewListener != null) {
            mViewListener!!.bindView(v)
        }
    }

    fun setFragmentManager(manager: FragmentManager?): BottomDialog {
        mFragmentManager = manager
        return this
    }

    fun setViewListener(listener: ViewListener?): BottomDialog {
        mViewListener = listener
        return this
    }

    fun setLayoutRes(@LayoutRes layoutRes: Int): BottomDialog {
        this.layoutRes = layoutRes
        return this
    }

    fun setCancelOutside(cancel: Boolean): BottomDialog {
        cancelOutside = cancel
        return this
    }

    fun setTag(tag: String?): BottomDialog {
        fragmentTag = tag
        return this
    }

    fun setDimAmount(dim: Float): BottomDialog {
        dimAmount = dim
        return this
    }

    fun setHeight(heightPx: Int): BottomDialog {
        height = heightPx
        return this
    }

    interface ViewListener {
        fun bindView(v: View?)
    }

    fun show(): BaseBottomDialog {
        show(mFragmentManager)
        return this
    }

    companion object {
        private const val KEY_LAYOUT_RES = "bottom_layout_res"
        private const val KEY_HEIGHT = "bottom_height"
        private const val KEY_DIM = "bottom_dim"
        private const val KEY_CANCEL_OUTSIDE = "bottom_cancel_outside"
        @JvmStatic
        fun create(manager: FragmentManager?): BottomDialog {
            val dialog = BottomDialog()
            dialog.fragmentManager = manager
            return dialog
        }
    }
}