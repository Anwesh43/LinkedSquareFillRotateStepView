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

class SquareFillRotateStepView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

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
}