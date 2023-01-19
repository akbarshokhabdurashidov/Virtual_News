package com.shakhrashidov.virtual_news.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.shakhrashidov.virtual_news.widget.DataProvider

class WidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return DataProvider(this, intent)
    }
}