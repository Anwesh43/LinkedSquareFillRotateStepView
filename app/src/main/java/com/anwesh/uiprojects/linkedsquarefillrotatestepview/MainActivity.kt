package com.anwesh.uiprojects.linkedsquarefillrotatestepview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.squarefillrotatestepview.SquareFillRotateStepView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view : SquareFillRotateStepView = SquareFillRotateStepView.create(this)
    }
}
