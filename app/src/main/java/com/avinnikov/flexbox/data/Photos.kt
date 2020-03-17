package com.avinnikov.flexbox.data

import android.widget.LinearLayout

data class Photos(
    val orientation: Int = LinearLayout.VERTICAL,
    val numberOfColumns: Int = 2,
    val counts: List<Int> = listOf(1, 2)
)