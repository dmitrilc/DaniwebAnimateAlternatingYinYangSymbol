package com.hoang.daniwebanimatealternatingyinyangsymbol

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.core.graphics.ColorUtils

private const val TAG = "MAIN_ACTIVITY"

class MainActivity : AppCompatActivity() {
    private var isInverted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val yinYang = findViewById<ImageView>(R.id.yin_yang).apply {
            setImageDrawable(YinYang(0f, isInverted))
        }

        yinYang.setOnClickListener {
            ValueAnimator.ofFloat(0f, 0.25f, 0f).apply {
                duration = 2_000L
                addUpdateListener { animator ->
                    yinYang.setImageDrawable(
                        YinYang(
                            animator.animatedValue as Float,
                            isInverted,
                            animatedFraction
                        )
                    )
                }
                doOnEnd {
                    isInverted = !isInverted
                }
                start()
            }
        }
    }
}

class YinYang(
    private val animatedValue: Float,
    private val isInverted: Boolean,
    private val animatedFraction: Float = 0f
): Drawable() {
    private val yinPaint = Paint().apply {
        color =  if (isInverted) {
            ColorUtils.blendARGB(
                Color.WHITE,
                Color.BLACK,
                animatedFraction
            )
        } else {
            ColorUtils.blendARGB(
                Color.BLACK,
                Color.WHITE,
                animatedFraction
            )
        }
    }

    private val yangPaint = Paint().apply {
        color =  if (isInverted) {
            ColorUtils.blendARGB(
                Color.BLACK,
                Color.WHITE,
                animatedFraction
            )
        } else {
            ColorUtils.blendARGB(
                Color.WHITE,
                Color.BLACK,
                animatedFraction
            )
        }
    }

    override fun draw(canvas: Canvas) {
        drawYang(canvas)
        drawYin(canvas)
        drawYangDot(canvas)
        drawYinDot(canvas)
    }

    private fun drawYang(canvas: Canvas){
        canvas.drawCircle(
            bounds.exactCenterX(),
            bounds.exactCenterY(),
            bounds.exactCenterX(),
            yangPaint
        )
    }

    private fun drawYin(canvas: Canvas){
        val yinPath = Path().apply {
            addArc(
                bounds.right * (0.25f + animatedValue), // 0.25 = full arc visible. 0.5 = arc invisible
                bounds.exactCenterY(),
                bounds.right * (0.75f - animatedValue), // 0.75 = full arc visible. 0.5 = arc invisible
                bounds.bottom.toFloat(),
                270f,
                if (animatedFraction < 0.5f)
                    if (isInverted) 180f
                    else -180f
                else
                    if (isInverted) -180f
                    else 180f
            )

            arcTo(
                0f,
                0f,
                bounds.right.toFloat(),
                bounds.bottom.toFloat(),
                90f,
                -180f,
                false
            )

            arcTo(
                bounds.right * (0.25f + animatedValue),
                0f,
                bounds.right * (0.75f - animatedValue),
                bounds.exactCenterY(),
                270f,
                if (animatedFraction < 0.5f)
                    if (isInverted) -180f
                    else 180f
                else
                    if (isInverted) 180f
                    else -180f,
                false
            )
        }

        canvas.drawPath(yinPath, yinPaint)
    }

    private fun drawYangDot(canvas: Canvas){
        val yangDotPath = Path().apply {
            addArc(
                bounds.right * 0.25f,
                bounds.bottom * 0.25f,
                bounds.right * 0.75f,
                bounds.bottom * 0.75f,
                if (isInverted) 270f
                else 90f,
                if (isInverted) 180f
                else -180f
            )
        }

        //Use for debugging
/*        canvas.drawPath(
            yangDotPath,
            Paint().apply {
                color = Color.RED
                style = Paint.Style.STROKE
                strokeWidth = 10f
            }
        )*/

        // Finds current position on arc at animation frame
        val yangDotPosition = FloatArray(2)
        PathMeasure(yangDotPath, false).apply {
            getPosTan(length * animatedFraction, yangDotPosition, null)
        }

        canvas.drawCircle(
            yangDotPosition[0],
            yangDotPosition[1],
            bounds.right * 0.07f,
            yangPaint
        )
    }

    private fun drawYinDot(canvas: Canvas){
        val yinDotPath = Path().apply {
            addArc(
                bounds.right * 0.25f,
                bounds.bottom * 0.25f,
                bounds.right * 0.75f,
                bounds.bottom * 0.75f,
                if (isInverted) 90f
                else 270f,
                if (isInverted) 180f
                else -180f
            )
        }

        //Use for debugging
/*        canvas.drawPath(
            yinDotPath,
            Paint().apply {
                color = Color.BLUE
                style = Paint.Style.STROKE
                strokeWidth = 10f
            }
        )*/

        val yinDotPosition = FloatArray(2)

        PathMeasure(yinDotPath, false).apply {
            getPosTan(length * animatedFraction, yinDotPosition, null)
        }

        canvas.drawCircle(
            yinDotPosition[0],
            yinDotPosition[1],
            bounds.right * 0.07f,
            yinPaint
        )
    }

    override fun setAlpha(alpha: Int) {
        // no op
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        // no op
    }

    @Deprecated("Deprecated in Superclass")
    override fun getOpacity() = PixelFormat.OPAQUE
}
