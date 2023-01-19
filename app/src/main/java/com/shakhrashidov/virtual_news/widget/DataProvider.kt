package com.shakhrashidov.virtual_news.widget

import android.R.id.text1
import android.R.layout.*
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService.RemoteViewsFactory

class DataProvider(context: Context?, intent: Intent?) : RemoteViewsFactory {

    var myListView: MutableList<String> = ArrayList()
    var mContext: Context? = null

    init {
        mContext = context
    }

    override fun onCreate() {
        initData()
    }

    override fun onDataSetChanged() {
        initData()
    }

    override fun onDestroy() {}
    override fun getCount(): Int {
        return myListView.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val view = RemoteViews(
            mContext!!.packageName,
            simple_list_item_1
        )
        view.setTextViewText(text1, myListView[position])
        return view
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    private fun initData() {
        myListView.clear()
        for (i in 1..5) {
//            myListView.add(ArrayList)
        }
    }

//    fun recData(): ArrayList<RecData> {
//        return arrayListOf(
//
//        )
//    }

}