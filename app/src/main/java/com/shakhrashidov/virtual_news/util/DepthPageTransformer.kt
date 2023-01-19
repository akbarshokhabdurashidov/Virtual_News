package com.shakhrashidov.virtual_news.util

import android.view.View
import androidx.annotation.RequiresApi
import androidx.viewpager2.widget.ViewPager2

private const val MIN_SCALE = 0.8f

@RequiresApi(21)
class DepthPageTransformer : ViewPager2.PageTransformer {

    override fun transformPage(view: View, position: Float) {
        view.apply {
            val pageWidth = width
            when {
                position < -1 -> {
                    alpha = 0f
                }
                position <= 0 -> {
                    alpha = 1f

                    translationY = 0f
                    scaleX = 1f
                    scaleY = 1f
                }
                position <= 1 -> {

                    alpha = 1 - position

                    translationY =-1 * view.width * position



                    val scaleFactor = (MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position)))
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                }
                else -> { // (1,+Infinity]

                    alpha = 0f
                }
            }
        }
    }
}