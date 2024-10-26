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
import android.util.TypedValue
import android.view.View
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


    private var buttonIdleColor = 0
    private var buttonLoadingColor = 0
    private var circleLoadingColor = 0

    private var widthSize = 0
    private var heightSize = 0
    private var textWidth = 0f

    private var buttonString = ""

    private var buttonTextColor = 0

    private var progressWidth = 0f
    private var progressCircle = 0f
    private var circleXOffset = 0f


    val fontSizeInSp = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, 20f, resources.displayMetrics)


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("", Typeface.BOLD)
        textSize = fontSizeInSp
    }

    private var valueAnimator = ValueAnimator()

    private var buttonStateObservable: ButtonStatus by Delegates.observable<ButtonStatus>(
        ButtonStatus.IDLE
    ) { p, old, new ->

        when (new) {
            ButtonStatus.IDLE -> {
                ValueAnimator.ofFloat(0f, 1f).apply {
                    duration = 1000
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator) {
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            buttonString = resources.getString(R.string.button_download)
                            invalidate()
                        }
                    })
                    start()
                }
            }


            ButtonStatus.LOADING -> {
                buttonString = resources.getString(R.string.button_loading)
                valueAnimator = ValueAnimator.ofFloat(0f, 1f)
                valueAnimator.setDuration(3000)
                valueAnimator.repeatCount = ValueAnimator.INFINITE
                valueAnimator.disableViewDuringAnimation(this)
                valueAnimator.addUpdateListener { animation ->
                    progressWidth = widthSize * animation.animatedValue as Float
                    progressCircle = 360 * animation.animatedValue as Float
                    invalidate()
                }
                valueAnimator.start()
                invalidate()
            }

            ButtonStatus.COMPLETED -> {
                buttonString = resources.getString(R.string.button_status_completed)
                valueAnimator.cancel()
                progressWidth = 0f
                progressCircle = 0f
                invalidate()
                buttonStateObservable = buttonStateObservable.next()
            }
        }
    }

    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingView) {
            buttonIdleColor = getColor(R.styleable.LoadingView_buttonIdleColor, 0)
            buttonLoadingColor = getColor(R.styleable.LoadingView_buttonLoadingColor, 0)
            circleLoadingColor = getColor(R.styleable.LoadingView_circleLoadingColor, 0)
        }
        buttonString = resources.getString(R.string.button_download)
        buttonTextColor = resources.getColor(R.color.white)

        circleXOffset = resources.getDimension(R.dimen.default_text_size) / 2

    }



    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }



    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)



        paint.color = buttonIdleColor
        canvas.drawRoundRect(
            0f, 0f, widthSize.toFloat(), heightSize.toFloat(), 10f, 10f, paint
        )

        paint.color = buttonLoadingColor

        canvas.drawRoundRect(
            0f, 0f, progressWidth.toFloat(), heightSize.toFloat(), 10f, 10f, paint
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
        canvas.drawArc(
            RectF(0f, 0f, paint.textSize, paint.textSize), 0F, progressCircle, true, paint
        )
        canvas.restore()

    }

    fun onCompleteDone() {
        buttonStateObservable = buttonStateObservable.next()
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

