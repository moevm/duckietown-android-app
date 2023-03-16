package com.etu.duckietownandroid

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import kotlin.math.pow
import kotlin.math.sqrt

private const val LONG_CLICK_TIME = 1000  // MilliSeconds
private const val CLICK_DIST = 0.05f

class MapImageView : androidx.appcompat.widget.AppCompatImageView {

    private var lastX = 0f
    private var lastY = 0f
    private var currentOffsetX = 0f
    private var currentOffsetY = 0f
    private val bounds = RectF()
    private var leftLimit = 0f
    private var topLimit = 0f
    private var rightLimit = 0f
    private var bottomLimit = 0f
    private var startOffset = Pair(0f, 0f)

    // Paint parameters

    private val firstPainter = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.GREEN
    }
    private val secondPainter = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.RED
    }
    private val borderPainter = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.DKGRAY
    }

    private var circleSize = 70
    private var circleBorder = 5

    var firstPoint: Pair<Float, Float>? = null
    var secondPoint: Pair<Float, Float>? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.MapImageView, defStyle, 0
        )



        circleSize = a.getDimensionPixelSize(R.styleable.MapImageView_pointSize, circleSize)
        firstPainter.color = a.getColor(R.styleable.MapImageView_firstPointFillColor, firstPainter.color)
        secondPainter.color = a.getColor(R.styleable.MapImageView_secondPointFillColor, secondPainter.color)
        borderPainter.strokeWidth = a.getDimensionPixelSize(R.styleable.MapImageView_pointBorder, circleBorder).toFloat()
        borderPainter.color = a.getColor(R.styleable.MapImageView_pointStrokeColor, borderPainter.color)

        a.recycle()

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        setTheBoundary()
    }

    override fun setImageMatrix(matrix: Matrix?) {
        super.setImageMatrix(matrix)
        setTheBoundary()
    }

    private fun setTheBoundary() {
        imageMatrix.mapRect(bounds, RectF(drawable.bounds))
        leftLimit = if (bounds.left < 0) bounds.left else 0f
        topLimit = if (bounds.top < 0) bounds.top else 0f
        rightLimit = (if (bounds.right > width) bounds.right else width.toFloat()) - width
        bottomLimit = (if (bounds.bottom > height) bounds.bottom else height.toFloat()) - height
    }

    private fun drawPoint(canvas: Canvas, x: Float, y: Float, painter: Paint){
        val pointX = paddingLeft + x
        val pointY = paddingTop + y
        canvas.drawCircle(pointX, pointY,
            circleSize.toFloat(), painter)
        canvas.drawCircle(pointX, pointY,
            circleSize.toFloat(), borderPainter)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        firstPoint?.apply {
            drawPoint(canvas, first, second, firstPainter)
        }
        secondPoint?.apply {
            drawPoint(canvas, first, second, secondPainter)
        }

    }

    fun clearPoints(){
        firstPoint = null
        secondPoint = null
        invalidate()
    }

    private fun setPoint(x: Float, y: Float){
        if(firstPoint == null){
            firstPoint = Pair(x + currentOffsetX, y + currentOffsetY)
        }else if(secondPoint == null){
            secondPoint = Pair(x + currentOffsetX, y + currentOffsetY)
        }
    }

    private fun checkCurrentOffset(currentX: Float, currentY: Float): Boolean{
        val dst = sqrt((currentX - startOffset.first).pow(2) + (currentY - startOffset.second).pow(2))
        return dst / width < CLICK_DIST && dst / height < CLICK_DIST
    }

    // TODO(What to do with people with disabilities?)
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX
                lastY = event.rawY
                event.apply { startOffset = Pair(x, y) }
            }
            MotionEvent.ACTION_MOVE -> {
                var disX = (event.rawX - lastX).toInt()
                var disY = (event.rawY - lastY).toInt()

                if (scrollX - disX < leftLimit) {
                    disX = (scrollX.toFloat() - leftLimit).toInt()
                } else if (scrollX - disX > rightLimit) {
                    disX = (scrollX.toFloat() - rightLimit).toInt()
                }

                if (scrollY - disY < topLimit) {
                    disY = (scrollY.toFloat() - topLimit).toInt()
                } else if (scrollY - disY > bottomLimit) {
                    disY = (scrollY.toFloat() - bottomLimit).toInt()
                }

                scrollBy(-disX, -disY)

                currentOffsetX -= disX
                currentOffsetY -= disY
                lastX = event.rawX
                lastY = event.rawY
            }
            MotionEvent.ACTION_UP -> {
                if((firstPoint == null || secondPoint == null) &&
                        event.eventTime - event.downTime < LONG_CLICK_TIME &&
                        checkCurrentOffset(event.x, event.y)){
                    setPoint(event.x, event.y)
                    Log.d("UI", "OnTouchEvent $firstPoint")
                    invalidate()
                }
            }
        }
        super.onTouchEvent(event)
        return true
    }

}