package com.anwesh.uiprojects.squarefillrotatestepview

/**
 * Created by anweshmishra on 12/11/18.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.Color
import android.app.Activity
import android.content.Context
import android.util.Log

val nodes : Int = 5

val rects : Int = 4

val STROKE_FACTOR : Int = 60

val SIZE_FACTOR : Int = 3

val SC_GAP : Float = 0.05f

val FOREGROUND_COLOR : Int = Color.parseColor("#4A148C")

fun Int.getInverse() : Float = 1f / this

fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.getInverse(), Math.max(0f, this - n.getInverse() * i)) * n

fun Float.getScaleFactor() : Float = Math.floor(this/0.5).toFloat()

fun Float.updateScaleBy(dir : Float) : Float = SC_GAP * dir * ((1 - getScaleFactor()) * rects.getInverse() + getScaleFactor())

fun Canvas.drawSFRSNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val size : Float = gap / SIZE_FACTOR
    paint.strokeWidth = Math.min(w, h) / STROKE_FACTOR
    paint.strokeCap = Paint.Cap.ROUND
    paint.color = FOREGROUND_COLOR
    val sc1 : Float = scale.divideScale(0, 2)
    val sc2 : Float = scale.divideScale(1, 2)
    val deg : Float = 360f / rects
    save()
    translate(gap * (i + 1), h/2)
    rotate(90f * sc2)
    paint.style = Paint.Style.STROKE
    drawRect(RectF(-size, -size, size, size), paint)
    for (j in 0..(rects - 1)) {
        val sc : Float = sc1.divideScale(j, rects)
        save()
        rotate(deg * j)
        drawRect(RectF(0f, 0f, size * sc, size * sc), paint)
        restore()
    }
    restore()
}

class SquareFillRotateStepView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            val k : Float = scale.updateScaleBy(dir)
            scale += k
            Log.d("update scale by", "$k")
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch (ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class SFRSNode(var i : Int, val state : State = State()) {

        private var prev : SFRSNode? = null

        private var next : SFRSNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = SFRSNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawSFRSNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : SFRSNode {
            var curr : SFRSNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class SquareFillRotateStep(var i : Int) {
        private val root : SFRSNode = SFRSNode(0)
        private var curr : SFRSNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb  : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : SquareFillRotateStepView) {

        private val sfrs : SquareFillRotateStep = SquareFillRotateStep(0)

        private val animator : Animator = Animator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#BDBDBD"))
            sfrs.draw(canvas, paint)
            animator.animate {
                sfrs.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            sfrs.startUpdating {
                animator.start()
            }
        }

    }
}