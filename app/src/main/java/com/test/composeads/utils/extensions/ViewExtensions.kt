package com.test.composeads.utils.extensions

import android.view.View

fun View.makeVisible() {
    visibility = View.VISIBLE
}
fun View.makeGone(){
    visibility = View.GONE
}
fun View.makeInvisible(){
    visibility = View.INVISIBLE
}