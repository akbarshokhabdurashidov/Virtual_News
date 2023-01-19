package com.shakhrashidov.virtual_news.util

import com.shakhrashidov.virtual_news.model.Article

sealed class TopBarState(
    val selectedItem:MutableList<Article>?=null
) {
    class NormalState() : TopBarState()
    class SearchState(val query: String? = null) : TopBarState()
    class CategoryState(val category: String? = null) : TopBarState()
    class SelectionState(private var selectedItemList: MutableList<Article>) : TopBarState(selectedItemList)

}