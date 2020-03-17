package com.avinnikov.flexbox

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.avinnikov.flexbox.data.Photos
import com.avinnikov.flexbox.data.RectObject
import com.avinnikov.flexbox.extensions.randomColor
import com.avinnikov.flexbox.ui.DragShadowBuilder
import java.util.concurrent.atomic.AtomicInteger

class MainActivity : AppCompatActivity() {

    //private val photos = Photos()
    private val photos = Photos(LinearLayout.HORIZONTAL, 2, listOf(2, 3))
    //private val photos = Photos(LinearLayout.VERTICAL, 3, listOf(10, 1, 3))
    private var draggableObject: RectObject? = null
    private val seed = AtomicInteger()

    private val dragListen = View.OnDragListener { v, event ->
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        v.background.colorFilter = BlendModeColorFilter(
                            ContextCompat.getColor(
                                baseContext,
                                R.color.blueFilter
                            ), BlendMode.MULTIPLY
                        )
                    } else {
                        v.background.setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY)
                    }

                    v.invalidate()
                    true
                } else {
                    false
                }
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    v.background.colorFilter = BlendModeColorFilter(
                        ContextCompat.getColor(
                            baseContext,
                            R.color.greenFilter
                        ), BlendMode.MULTIPLY
                    )
                } else {
                    v.background.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY)
                }

                draggableObject ?: run {
                    draggableObject = RectObject(v.background as GradientDrawable, v.id)
                }

                v.invalidate()
                true
            }

            DragEvent.ACTION_DRAG_LOCATION -> true
            DragEvent.ACTION_DRAG_EXITED -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    v.background.colorFilter = BlendModeColorFilter(
                        ContextCompat.getColor(
                            baseContext,
                            R.color.blueFilter
                        ), BlendMode.MULTIPLY
                    )
                } else {
                    v.background.setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY)
                }

                v.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                v.background.clearColorFilter()

                val firstView = findViewById<View>(draggableObject?.id!!)
                firstView.background = v.background as GradientDrawable
                v.background = draggableObject?.drawable
                draggableObject = null

                v.invalidate()
                true
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                v.background.clearColorFilter()

                v.invalidate()
                true
            }
            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val globalLayout = LinearLayout(this).apply {
            orientation = if (photos.orientation == LinearLayout.VERTICAL) LinearLayout.HORIZONTAL
            else LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }
        setContentView(globalLayout)

        drawColumns(globalLayout)
    }

    private fun drawColumns(layout: LinearLayout) {
        val layoutWeight = 1f / photos.numberOfColumns

        repeat(photos.numberOfColumns) {
            val columnLayout = LinearLayout(this).apply {
                orientation = photos.orientation
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = layoutWeight
                }
            }

            drawRects(columnLayout, photos.counts[it])
            layout.addView(columnLayout)
        }
    }

    private fun drawRects(layout: LinearLayout, number: Int) {
        val layoutWeight = 1f / number

        repeat(number) {
            val view = View(this).apply {
                background = GradientDrawable().apply {
                    setColor(randomColor())
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = layoutWeight
                    setMargins(50, 50, 50, 50)
                }

                val curId = seed.incrementAndGet()
                id = curId
                tag = "VIEW_TAG_$curId"
                setOnLongClickListener {
                    val item = ClipData.Item(it.tag as? CharSequence)

                    val dragData = ClipData(
                        it.tag as? CharSequence,
                        arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                        item
                    )

                    val myShadow = DragShadowBuilder(this)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        it.startDragAndDrop(
                            dragData,
                            myShadow,
                            null,
                            0
                        )
                    } else {
                        it.startDrag(
                            dragData,
                            myShadow,
                            null,
                            0
                        )
                    }
                }
                setOnDragListener(dragListen)
            }
            layout.addView(view)
        }
    }
}