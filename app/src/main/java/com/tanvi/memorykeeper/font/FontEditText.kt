package com.tanvi.memorykeeper.font

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.EditText

@SuppressLint("AppCompatCustomView")
class FontEditText : EditText {
    constructor(context: Context) : super(context) {
        val face =
            Typeface.createFromAsset(context.assets, "fonts/OpenSans-Regular.ttf")
        this.typeface = face
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        val face =
            Typeface.createFromAsset(context.assets, "fonts/OpenSans-Regular.ttf")
        this.typeface = face
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        val face =
            Typeface.createFromAsset(context.assets, "fonts/OpenSans-Regular.ttf")
        this.typeface = face
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}