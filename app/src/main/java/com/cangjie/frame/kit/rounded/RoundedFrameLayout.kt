package com.cangjie.frame.kit.rounded

import android.widget.FrameLayout
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import cangjie.scale.sorting.R
import androidx.annotation.Px
import androidx.core.view.ViewCompat

class RoundedFrameLayout : FrameLayout {
    private val mClipPath = Path()
    private val mPaint = Paint()
    private val mLayoutBounds = RectF()
    private val mRoundCornerRadii = FloatArray(8)
    private var mIsLaidOut = false
    private var mRoundedCornerRadius = 0
    private var mRoundAsCircle = false

    /**
     * Gets whether to enable top left fillet
     *
     * @return Returns whether to enable top left fillet
     */
    var isRoundTopLeft = false
        private set

    /**
     * Gets whether to enable top right fillet
     *
     * @return Returns whether to enable top right fillet
     */
    var isRoundTopRight = false
        private set

    /**
     * Gets whether to enable bottom left fillet
     *
     * @return Returns whether to enable bottom left fillet
     */
    var isRoundBottomLeft = false
        private set

    /**
     * Gets whether to enable bottom right fillet
     *
     * @return Returns whether to enable bottom right fillet
     */
    var isRoundBottomRight = false
        private set
    private var mRoundingBorderWidth = 0
    private var mRoundingBorderColor = 0
    private var mRoundingElevation = 0f
    private val mBackground = GradientDrawable()

    constructor(context: Context) : super(context) {
        initLayouts(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initLayouts(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initLayouts(context, attrs, defStyleAttr, 0)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initLayouts(context, attrs, defStyleAttr, defStyleRes)
    }

    private fun initLayouts(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        if (isInEditMode) {
            return
        }
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.RoundedLayout,
            defStyleAttr,
            defStyleRes
        )
        mRoundedCornerRadius =
            a.getDimensionPixelSize(R.styleable.RoundedLayout_rlRoundedCornerRadius, 0)
        mRoundAsCircle = a.getBoolean(R.styleable.RoundedLayout_rlRoundAsCircle, false)
        isRoundTopLeft = a.getBoolean(R.styleable.RoundedLayout_rlRoundTopLeft, true)
        isRoundTopRight = a.getBoolean(R.styleable.RoundedLayout_rlRoundTopRight, true)
        isRoundBottomLeft = a.getBoolean(R.styleable.RoundedLayout_rlRoundBottomLeft, true)
        isRoundBottomRight = a.getBoolean(R.styleable.RoundedLayout_rlRoundBottomRight, true)
        mRoundingBorderWidth =
            a.getDimensionPixelSize(R.styleable.RoundedLayout_rlRoundingBorderWidth, 0)
        mRoundingBorderColor = a.getColor(R.styleable.RoundedLayout_rlRoundingBorderColor, 0)
        if (a.hasValue(R.styleable.RoundedLayout_rlRoundingElevation)) {
            mRoundingElevation =
                a.getDimensionPixelSize(R.styleable.RoundedLayout_rlRoundingElevation, 0).toFloat()
        }
        a.recycle()
        roundingElevation = mRoundingElevation
        mPaint.isAntiAlias = true
        mPaint.color = mRoundingBorderColor
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = (mRoundingBorderWidth * 2).toFloat()
        mBackground.setColor(mRoundingBorderColor or -0x1000000)
        mBackground.cornerRadii = buildRoundCornerRadii(mRoundedCornerRadius.toFloat())
        super.setBackgroundDrawable(mBackground)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun setElevation(@Px elevation: Float) {
        super.setElevation(elevation)
        mRoundingElevation = elevation
    }

    override fun setBackground(background: Drawable) {
        // super.setBackground(background);
    }

    override fun setBackgroundDrawable(background: Drawable) {
        // super.setBackgroundDrawable(background);
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        adjustClipPathBounds()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mLayoutBounds[0f, 0f, (right - left).toFloat()] = (bottom - top).toFloat()
        if (!mIsLaidOut) {
            mIsLaidOut = true
            adjustClipPathBounds()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mIsLaidOut = false
    }

    private fun adjustClipPathBounds() {
        if (!mIsLaidOut) {
            return
        }
        var cornerRadius = mRoundedCornerRadius.toFloat()
        if (mRoundAsCircle) {
            cornerRadius = Math.max(mLayoutBounds.width(), mLayoutBounds.height()) / 2f
        }
        mClipPath.reset()
        mClipPath.addRoundRect(
            mLayoutBounds,
            buildRoundCornerRadii(cornerRadius),
            Path.Direction.CW
        )
        mClipPath.close()
        mBackground.cornerRadii = buildRoundCornerRadii(cornerRadius)
    }

    private fun buildRoundCornerRadii(cornerRadius: Float): FloatArray {
        mRoundCornerRadii[0] = if (isRoundTopLeft) cornerRadius else 0f
        mRoundCornerRadii[1] = if (isRoundTopLeft) cornerRadius else 0f
        mRoundCornerRadii[2] = if (isRoundTopRight) cornerRadius else 0f
        mRoundCornerRadii[3] = if (isRoundTopRight) cornerRadius else 0f
        mRoundCornerRadii[4] = if (isRoundBottomRight) cornerRadius else 0f
        mRoundCornerRadii[5] = if (isRoundBottomRight) cornerRadius else 0f
        mRoundCornerRadii[6] = if (isRoundBottomLeft) cornerRadius else 0f
        mRoundCornerRadii[7] = if (isRoundBottomLeft) cornerRadius else 0f
        return mRoundCornerRadii
    }

    override fun draw(canvas: Canvas) {
        canvas.clipPath(mClipPath)
        super.draw(canvas)
        if (mRoundingBorderWidth > 0 && mRoundingBorderColor != 0) {
            canvas.drawPath(mClipPath, mPaint)
        }
    }

    /**
     * Set the radius of the corner angle and whether the edges are enabled
     *
     * @param cornerRadius The radius of the angle of the circle. Unit px
     * @param topLeft      Whether to enable top left fillet
     * @param topRight     Whether to enable top right fillet
     * @param bottomLeft   Whether to enable bottom left fillet
     * @param bottomRight  Whether to enable bottom right fillet
     */
    fun setRoundedCornerRadius(
        cornerRadius: Int, topLeft: Boolean, topRight: Boolean,
        bottomRight: Boolean, bottomLeft: Boolean
    ) {
        if (mRoundedCornerRadius != cornerRadius || isRoundTopLeft != topLeft || isRoundTopRight != topRight || isRoundBottomLeft != bottomLeft || isRoundBottomRight != bottomRight) {
            mRoundedCornerRadius = cornerRadius
            isRoundTopLeft = topLeft
            isRoundTopRight = topRight
            isRoundBottomLeft = bottomLeft
            isRoundBottomRight = bottomRight
            adjustClipPathBounds()
            postInvalidate()
        }
    }
    /**
     * Gets the angle of the corner of the View fillet.
     *
     * @return Returns the current angle of the corner of the View fillet
     */
    /**
     * Set the radius of the corner angle and whether the edges are enabled
     *
     * @param cornerRadius The radius of the angle of the circle. Unit px
     */
    var roundedCornerRadius: Int
        get() = mRoundedCornerRadius
        set(cornerRadius) {
            setRoundedCornerRadius(cornerRadius, true, true, true, true)
        }
    /**
     * Gets whether to round as circle
     *
     * @return Returns whether to round as circle
     */
    /**
     * Set's whether to round as circle
     *
     * @param asCircle Whether to round as circle
     */
    var isRoundAsCircle: Boolean
        get() = mRoundAsCircle
        set(asCircle) {
            if (asCircle != mRoundAsCircle) {
                mRoundAsCircle = asCircle
                adjustClipPathBounds()
                postInvalidate()
            }
        }
    /**
     * Gets the width of the View rounded border. Unit px
     *
     * @return Returns the current width of the View Rounded Border
     */
    /**
     * Setts the width of the View rounded border.
     *
     * @param width View the width of the rounded border . Unit px
     */
    var roundingBorderWidth: Int
        get() = mRoundingBorderWidth
        set(width) {
            if (width != mRoundingBorderWidth) {
                mRoundingBorderWidth = width
                mPaint.strokeWidth = (mRoundingBorderWidth * 2).toFloat()
                postInvalidate()
            }
        }
    /**
     * Gets the color of the View rounded border.
     *
     * @return Returns the current color of the View Rounded Border
     */
    /**
     * Setts the color of the View rounded border.
     *
     * @param color View the color of the rounded border
     */
    var roundingBorderColor: Int
        get() = mRoundingBorderColor
        set(color) {
            if (color != mRoundingBorderColor) {
                mRoundingBorderColor = color
                mPaint.color = mRoundingBorderColor
                mBackground.setColor(mRoundingBorderColor or -0x1000000)
                postInvalidate()
            }
        }
    /**
     * The base elevation of this view relative to its parent, in pixels.
     *
     * @return The base depth position of the view, in pixels.
     */
    /**
     * Sets the base elevation of this view, in pixels.
     *
     * @param elevation org.amphiaraus.roundedlayout.R.styleable#RoundedLayout_rlRoundingElevation
     */
    var roundingElevation: Float
        get() = mRoundingElevation
        set(elevation) {
            mRoundingElevation = elevation
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setElevation(elevation)
            } else {
                ViewCompat.setElevation(this, elevation)
            }
        }
}