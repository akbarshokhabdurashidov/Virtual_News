package com.shakhrashidov.virtual_news.util.swipeDetector

import androidx.recyclerview.widget.RecyclerView
import com.shakhrashidov.virtual_news.R

interface RecyclerViewSwipe {
    fun onSwipeLeft(viewHolder: RecyclerView.ViewHolder)
    fun onSwipeRight(viewHolder: RecyclerView.ViewHolder)
    fun  addSwipeLeftBackgroundColor():Int
    fun  addSwipeRightBackgroundColor():Int
    fun addSwipeLeftActionIcon():Int
    fun addSwipeRightActionIcon():Int = R.drawable.ic_share_2

}