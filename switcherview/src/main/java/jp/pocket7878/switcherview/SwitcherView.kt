package jp.pocket7878.switcherview

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.*
import androidx.core.widget.ImageViewCompat

class SwitcherView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    ConstraintLayout(context, attrs, defStyle) {

    interface OnSwitchSelectChangeListener {
        fun onLeftChoiceSelected()
        fun onRightChoiceSelected()
        fun onStartSwitchUserControl()
        fun onFinishSwitchUserControl()
    }

    private var listener: OnSwitchSelectChangeListener? = null

    private var root: View = LayoutInflater.from(context).inflate(
        R.layout.view_switcher,
        this,
        true
    )

    private val leftIconView by lazy { root.findViewById<ImageView>(R.id.left_icon_image_view) }
    private val leftTextView by lazy { root.findViewById<TextView>(R.id.left_text_view) }
    private val rightIconView by lazy { root.findViewById<ImageView>(R.id.right_icon_image_view) }
    private val rightTextView by lazy { root.findViewById<TextView>(R.id.right_text_view) }
    private val hoverCanvasView by lazy { root.findViewById<SwitcherHoverCanvasView>(R.id.hybrid_hover_canvas) }

    private var enableTintColor: Int = Color.WHITE
    private var disableTintColor: Int = Color.BLACK

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SwitcherView, defStyle, 0)

        enableTintColor =
            a.getColor(R.styleable.SwitcherView_sv_enable_choice_tint_color, Color.WHITE)
        disableTintColor =
            a.getColor(R.styleable.SwitcherView_sv_disable_choice_tint_color, Color.BLACK)

        leftIconView.setImageDrawable(a.getDrawable(R.styleable.SwitcherView_sv_left_choice_icon_src))
        leftTextView.text = a.getString(R.styleable.SwitcherView_sv_left_choice_text)
        updateLeftIconTextColor(0.0)

        rightIconView.setImageDrawable(a.getDrawable(R.styleable.SwitcherView_sv_right_choice_icon_src))
        rightTextView.text = a.getString(R.styleable.SwitcherView_sv_right_choice_text)
        updateRightIconTextColor(0.0)

        hoverCanvasView.apply {
            bgColor = a.getColor(R.styleable.SwitcherView_sv_background_color, Color.WHITE)
            leftMostHoverColor =
                a.getColor(R.styleable.SwitcherView_sv_leftmost_hover_color, Color.BLUE)
            rightMostHoverColor =
                a.getColor(R.styleable.SwitcherView_sv_rightmost_hover_color, Color.RED)
        }

        a.recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        hoverCanvasView.ratioChangeListener =
            object : SwitcherHoverCanvasView.OnRatioChangeListener {
                override fun onRatioChanged(ratio: Double) {
                    if (ratio < 0.0 || ratio > 1.0) throw IllegalStateException("Ratio must be in range 0.0 ~ 1.0 : $ratio")

                    updateLeftIconTextColor(ratio)
                    updateRightIconTextColor(ratio)
                }
            }

        hoverCanvasView.hoverTouchListener =
            object : SwitcherHoverCanvasView.OnHoverTouchListener {
                override fun onHoverTouchDown() {
                    listener?.onStartSwitchUserControl()
                }

                override fun onHoverTouchUp() {
                    listener?.onFinishSwitchUserControl()
                }
            }

        hoverCanvasView.hoverControlFinishListener =
            object : SwitcherHoverCanvasView.OnHoverControlFinishListener {
                override fun onControlFinishAtLeft() {
                    listener?.onLeftChoiceSelected()
                }

                override fun onControlFinishAtRight() {
                    listener?.onRightChoiceSelected()
                }
            }
    }

    fun setOnSwitchSelectChangeListener(listener: OnSwitchSelectChangeListener) {
        this.listener = listener
    }

    //region Control hover programmatically
    fun switchToLeftChoice() {
        hoverCanvasView.setRatio(0.0, true)
    }

    fun switchToRightChoice() {
        hoverCanvasView.setRatio(1.0, true)
    }
    //endregion

    //region Tint color update
    private fun updateLeftIconTextColor(ratio: Double) {
        if (ratio < 0.0 || ratio > 1.0) throw IllegalArgumentException("Ratio must be in range 0.0 ~ 1.0 : $ratio")

        leftItemTintColor(ratio).let {
            ImageViewCompat.setImageTintList(leftIconView, ColorStateList.valueOf(it))
            leftTextView.setTextColor(it)
        }
    }

    private fun updateRightIconTextColor(ratio: Double) {
        if (ratio < 0.0 || ratio > 1.0) throw IllegalArgumentException("Ratio must be in range 0.0 ~ 1.0 : $ratio")

        rightItemTintColor(ratio).let {
            ImageViewCompat.setImageTintList(rightIconView, ColorStateList.valueOf(it))
            rightTextView.setTextColor(it)
        }
    }
    //endregion

    //region Tint color calculator
    private fun leftItemTintColor(ratio: Double): Int {
        if (ratio < 0.0 || ratio > 1.0) throw IllegalArgumentException("Ratio must be in range 0.0 ~ 1.0 : $ratio")

        return tintColorOfEnableRatio(1.0 - ratio)
    }

    private fun rightItemTintColor(ratio: Double): Int {
        if (ratio < 0.0 || ratio > 1.0) throw IllegalArgumentException("Ratio must be in range 0.0 ~ 1.0 : $ratio")

        return tintColorOfEnableRatio(ratio)
    }

    private fun tintColorOfEnableRatio(ratio: Double): Int {
        if (ratio < 0.0 || ratio > 1.0) throw IllegalArgumentException("Ratio must be in range 0.0 ~ 1.0 : $ratio")

        return Color.rgb(
            (enableTintColor.red + (disableTintColor.red - enableTintColor.red) * (1.0 - ratio)).toInt(),
            (enableTintColor.green + (disableTintColor.green - enableTintColor.green) * (1.0 - ratio)).toInt(),
            (enableTintColor.blue + (disableTintColor.blue - enableTintColor.blue) * (1.0 - ratio)).toInt()
        )
    }
    //endregion

    private class SwitcherHoverCanvasView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
    ) : View(context, attrs, defStyle) {
        interface OnRatioChangeListener {
            fun onRatioChanged(ratio: Double)
        }

        interface OnHoverTouchListener {
            fun onHoverTouchUp()
            fun onHoverTouchDown()
        }

        interface OnHoverControlFinishListener {
            fun onControlFinishAtLeft()
            fun onControlFinishAtRight()
        }

        var ratioChangeListener: OnRatioChangeListener? = null
        var hoverTouchListener: OnHoverTouchListener? = null
        var hoverControlFinishListener: OnHoverControlFinishListener? = null

        private var ratio: Double = 0.0

        /*
         * Background
         */

        var bgColor: Int = Color.TRANSPARENT
        private val contentWidth: Int
            get() {
                return width - paddingLeft - paddingRight
            }
        private val contentHeight: Int
            get() {
                return height - paddingTop - paddingBottom
            }
        private val backgroundRect: Rect
            get() {
                return Rect(
                    paddingLeft,
                    paddingTop,
                    paddingLeft + contentWidth,
                    paddingTop + contentHeight
                )
            }
        private val bgPaint: Paint
            get() {
                return Paint().also {
                    it.color = bgColor
                    it.isAntiAlias = true
                }
            }

        /*
         * Hover
         */
        var leftMostHoverColor: Int = Color.BLUE
        var rightMostHoverColor: Int = Color.RED
        private val hoverColor: Int
            get() {
                val leftMostColor = leftMostHoverColor
                val rightMostColor = rightMostHoverColor
                return Color.argb(
                    (leftMostColor.alpha + (rightMostColor.alpha - leftMostColor.alpha) * this.ratio).toInt(),
                    (leftMostColor.red + (rightMostColor.red - leftMostColor.red) * this.ratio).toInt(),
                    (leftMostColor.green + (rightMostColor.green - leftMostColor.green) * this.ratio).toInt(),
                    (leftMostColor.blue + (rightMostColor.blue - leftMostColor.blue) * this.ratio).toInt()
                )
            }

        private val hoverRect: Rect
            get() {
                val hoverHeight: Int = height - 8.dp
                val hoverTop: Int = 4.dp
                val hoverWidth: Int = contentWidth / 2 - (height - hoverHeight) / 2
                val hoverLeft: Int =
                    ((contentWidth / 2 - (height - hoverHeight) / 2) * this.ratio).toInt() + (height - hoverHeight) / 2
                return Rect(hoverLeft, hoverTop, hoverLeft + hoverWidth, hoverTop + hoverHeight)
            }
        private val hoverPaint: Paint
            get() {
                return Paint().also {
                    it.color = hoverColor
                    it.isAntiAlias = true
                }
            }

        private var draggingHover: Boolean = false

        init {
            val gestureDetector =
                GestureDetector(context, object : GestureDetector.OnGestureListener {
                    override fun onShowPress(p0: MotionEvent?) {
                    }

                    override fun onSingleTapUp(event: MotionEvent): Boolean {
                        return false
                    }

                    override fun onDown(p0: MotionEvent): Boolean {
                        onDownEvent(p0)
                        return true
                    }

                    override fun onFling(
                        p0: MotionEvent?,
                        p1: MotionEvent?,
                        p2: Float,
                        p3: Float
                    ): Boolean {
                        return false
                    }

                    override fun onScroll(
                        scrollStart: MotionEvent,
                        currentScroll: MotionEvent,
                        distanceX: Float,
                        distanceY: Float
                    ): Boolean {
                        onScrollEvent(distanceX.toDouble())
                        return true
                    }

                    override fun onLongPress(p0: MotionEvent?) {
                    }
                })

            setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        onUp(event)
                        true
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        onCancel()
                        true
                    }
                    else -> gestureDetector.onTouchEvent(event)
                }
            }
        }

        fun setRatio(newRatio: Double, animate: Boolean = false) {
            if (newRatio < 0.0 || newRatio > 1.0) {
                throw IllegalArgumentException("Ratio must be in range 0.0 ~ 1.0: $newRatio")
            }

            if (animate) {
                ValueAnimator.ofFloat(ratio.toFloat(), newRatio.toFloat()).apply {
                    duration = 200
                    addUpdateListener { animation ->
                        val currentValue = animation.animatedValue as Float
                        if (currentValue.toDouble() != this@SwitcherHoverCanvasView.ratio) {
                            this@SwitcherHoverCanvasView.ratio = currentValue.toDouble()
                            ratioChangeListener?.onRatioChanged(currentValue.toDouble())
                            invalidate()
                        }
                    }
                }.start()
            } else {
                if (newRatio != this.ratio) {
                    this.ratio = newRatio
                    ratioChangeListener?.onRatioChanged(newRatio)
                    invalidate()
                }
            }
        }

        private fun onDownEvent(event: MotionEvent) {
            inHoverArea(Point(event.x.toInt(), event.y.toInt())).let {
                this.draggingHover = it
                if (it) {
                    this.hoverTouchListener?.onHoverTouchDown()
                }
            }
            this.draggingHover = inHoverArea(Point(event.x.toInt(), event.y.toInt()))
        }

        private fun onCancel() {
            var newRatio: Double = this.ratio
            newRatio = if (newRatio < 0.5) {
                0.0
            } else {
                1.0
            }
            setRatio(newRatio, true)
            if (this.draggingHover) {
                this.hoverTouchListener?.onHoverTouchUp()
            }
            this.draggingHover = false
        }

        private fun onUp(event: MotionEvent) {
            var newRatio: Double = (event.x.toDouble() / width.toDouble()).coerceIn(0.0, 1.0)
            newRatio = if (newRatio < 0.5) {
                0.0
            } else {
                1.0
            }
            setRatio(newRatio, true)
            if (this.draggingHover) {
                this.hoverTouchListener?.onHoverTouchUp()
            }
            this.draggingHover = false
            when (newRatio) {
                0.0 -> hoverControlFinishListener?.onControlFinishAtLeft()
                1.0 -> hoverControlFinishListener?.onControlFinishAtRight()
            }
        }

        private fun onScrollEvent(distanceX: Double) {
            if (draggingHover) {
                val ratioDiff: Double = (distanceX * -1) / (width.toDouble() / 2.0)
                val newRatio: Double = (ratioDiff + ratio).coerceIn(0.0, 1.0)
                setRatio(newRatio, false)
            }
        }

        private fun inHoverArea(point: Point): Boolean {
            return hoverRect.contains(point)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            drawTube(canvas, backgroundRect, bgPaint)
            drawTube(canvas, hoverRect, hoverPaint)
        }

        private fun drawTube(canvas: Canvas, rect: Rect, paint: Paint) {
            val capRadius: Float = rect.height() / 2.0F

            val leftCx: Float = rect.left + capRadius
            val leftCy: Float = rect.top + capRadius
            canvas.drawCircle(leftCx, leftCy, capRadius, paint)

            val rightCx: Float = rect.right - capRadius
            val rightCy: Float = rect.top + capRadius
            canvas.drawCircle(rightCx, rightCy, capRadius, paint)

            val bodyRect = Rect(
                leftCx.toInt(),
                rect.top,
                rightCx.toInt(),
                rect.bottom
            )
            canvas.drawRect(bodyRect, paint)
        }
    }
}
