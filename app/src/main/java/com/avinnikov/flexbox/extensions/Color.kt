package com.avinnikov.flexbox.extensions

import android.graphics.Color
import kotlin.random.Random

fun randomColor(alpha: Int = 1000) = Color.argb(
    (0.255 * alpha).toInt(), Random.nextInt(256), Random.nextInt(256),
    Random.nextInt(256)
)