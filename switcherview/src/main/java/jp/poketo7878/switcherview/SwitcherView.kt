package jp.poketo7878.switcherview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import jp.poketo7878.switcherview.databinding.ViewSwitcherBinding

class SwitcherView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    ConstraintLayout(context, attrs, defStyle) {

    interface OnSwitchSelectChangeListener {
        fun onLeftItemSelected()
        fun onRightItemSelected()
        fun onStartSwitchUserControl()
        fun onFinishSwitchUserControl()
    }

    var listener: OnSwitchSelectChangeListener? = null

    private var binding: ViewSwitcherBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.view_switcher,
        this,
        true
    )

    private val leftIconView by lazy { binding.rightIconImageView }
    private val leftTextView by lazy { binding.leftTextView }
    private val rightIconView by lazy { binding.rightIconImageView }
    private val rightTextView by lazy { binding.rightTextView }
    private val hoverCanvasView by lazy { binding.hybridHoverCanvas }

    private var enableTintColor: Int = Color.WHITE
    private var disableTintColor: Int = Color.BLACK

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SwitcherView, defStyle, 0)

        enableTintColor = a.getColor(R.styleable.SwitcherView_enable_tint_color, Color.WHITE)
        disableTintColor = a.getColor(R.styleable.SwitcherView_disable_tint_color, Color.BLACK)

        leftIconView.setImageDrawable(a.getDrawable(R.styleable.SwitcherView_left_icon_src))
        binding.leftTextView.text = a.getString(R.styleable.SwitcherView_left_text)
        updateLeftIconTextColor(0.0)

        rightIconView.setImageDrawable(a.getDrawable(R.styleable.SwitcherView_right_icon_src))
        rightTextView.text = a.getString(R.styleable.SwitcherView_right_text)
        updateRightIconTextColor(0.0)

        binding.hybridHoverCanvas.apply {
            bgColor = a.getColor(R.styleable.SwitcherView_hover_base_color, Color.WHITE)
            leftMostHoverColor =
                a.getColor(R.styleable.SwitcherView_leftmost_hover_color, Color.BLUE)
            rightMostHoverColor =
                a.getColor(R.styleable.SwitcherView_rightmost_hover_color, Color.RED)
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
                    listener?.onLeftItemSelected()
                }

                override fun onControlFinishAtRight() {
                    listener?.onRightItemSelected()
                }
            }
    }

    //region Control hover programmatically
    fun switchToLeftItem() {
        hoverCanvasView.setRatio(0.0, true)
    }

    fun switchToRightItem() {
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
}
