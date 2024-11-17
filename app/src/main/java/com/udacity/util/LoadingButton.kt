package com.udacity.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import com.udacity.R
import kotlin.properties.Delegates


private enum class ButtonStatus(val label: Int) {

    IDLE(R.string.button_download), LOADING(R.string.button_loading), COMPLETED(
        R.string.button_status_completed
    );

    fun next() = when (this) {
        IDLE -> LOADING
        LOADING -> COMPLETED
        COMPLETED -> IDLE
    }
}

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0
    private var textWidth = 0f
    private var buttonString = ""
    private var progressWidth = 0f
    private var progressCircle = 0f
    private var circleXOffset = 0f
    private var paint: Paint
    private var valueAnimator = ValueAnimator()
    private val rectF = RectF() // Preallocate the RectF object

    // TODO : Allow Customization of Button from Code
    var buttonTextColor = 0
    var cornerRadius = 0f
    var buttonTextSize = 20f
    var isTextCaps = false
    var textStyle = 0
    private var buttonIdleColor = 0
    private var buttonLoadingColor = 0
    private var circleLoadingColor = 0

    private var buttonStateObservable: ButtonStatus by Delegates.observable<ButtonStatus>(
        ButtonStatus.IDLE
    ) { p, old, new ->
        when (new) {
            ButtonStatus.IDLE -> animateButton(R.string.button_download, 1000)
            ButtonStatus.LOADING -> startLoadingAnimation(R.string.button_loading)
            ButtonStatus.COMPLETED -> completeLoading(R.string.button_status_completed)
        }
    }

    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingView) {
            buttonIdleColor = getColor(R.styleable.LoadingView_buttonIdleColor, 0)
            buttonLoadingColor = getColor(R.styleable.LoadingView_buttonLoadingColor, 0)
            circleLoadingColor = getColor(R.styleable.LoadingView_circleLoadingColor, 0)
            cornerRadius = getDimension(R.styleable.LoadingView_cornerRadius, 32f)
            buttonTextSize = getDimension(R.styleable.LoadingView_textSize, 20f)
            isTextCaps = getBoolean(R.styleable.LoadingView_textAllCaps, false)
            textStyle = getInt(R.styleable.LoadingView_textStyling, 0)
        }
        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textSize = buttonTextSize
        }
        paint.typeface = when (textStyle) {
            1 -> Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            2 -> Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            3 -> Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
            else -> Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
        buttonString = resources.getString(R.string.button_download).let {
            if (isTextCaps) it.uppercase() else it
        }
        buttonTextColor = ResourcesCompat.getColor(resources, R.color.colorWhite, null)
        circleXOffset = resources.getDimension(R.dimen.default_text_size) / 2
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = buttonIdleColor
        canvas.drawRoundRect(
            0f,
            0f,
            widthSize.toFloat(),
            heightSize.toFloat(),
            cornerRadius,
            cornerRadius,
            paint
        )
        paint.color = buttonLoadingColor
        canvas.drawRoundRect(
            0f,
            0f,
            progressWidth.toFloat(),
            heightSize.toFloat(),
            cornerRadius,
            cornerRadius,
            paint
        )
        paint.color = buttonTextColor
        canvas.drawText(
            buttonString,
            (widthSize / 2).toFloat(),
            heightSize / 2 - (paint.descent() + paint.ascent()) / 2,
            paint
        )
        textWidth = paint.measureText(buttonString)
        canvas.save()
        canvas.translate(
            widthSize / 2 + textWidth / 2 + circleXOffset, heightSize / 2 - paint.textSize / 2
        )
        paint.color = circleLoadingColor
        rectF.set(0f, 0f, paint.textSize, paint.textSize)
        canvas.drawArc(
            rectF, 0F, progressCircle, true, paint
        )
        canvas.restore()
    }

    fun onCompleteDone() {
        buttonStateObservable = ButtonStatus.COMPLETED
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w), heightMeasureSpec, 0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun onClick() {
        contentDescription = resources.getString(buttonStateObservable.label)
        buttonStateObservable = ButtonStatus.LOADING
    }

    private fun animateButton(stringRes: Int, duration: Long) {
        ValueAnimator.ofFloat(0f, 1f).apply {
            this.duration = duration
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    buttonString = resources.getString(stringRes).let {
                        if (isTextCaps) it.uppercase() else it
                    }
                    invalidate()
                }
            })
            start()
        }
    }

    private fun startLoadingAnimation(stringRes: Int) {
        buttonString = resources.getString(stringRes).let {
            if (isTextCaps) it.uppercase() else it
        }
        valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 3000
            repeatCount = ValueAnimator.INFINITE
            disableViewDuringAnimation(this@LoadingButton)
            addUpdateListener { animation ->
                progressWidth = widthSize * animation.animatedValue as Float
                progressCircle = 360 * animation.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun completeLoading(stringRes: Int) {
        buttonString = resources.getString(stringRes).let {
            if (isTextCaps) it.uppercase() else it
        }
        valueAnimator.cancel()
        progressWidth = 0f
        progressCircle = 0f
        invalidate()
        buttonStateObservable = buttonStateObservable.next()
    }

    private fun ValueAnimator.disableViewDuringAnimation(view: View) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator) {
                view.isEnabled = true
                progressWidth = 0f
                if (buttonStateObservable == ButtonStatus.LOADING) {
                    buttonStateObservable = buttonStateObservable.next()
                }
            }
        })
    }
}

