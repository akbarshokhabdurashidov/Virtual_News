package com.shakhrashidov.virtual_news.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.NonNull
import com.shakhrashidov.virtual_news.R


class CollectionWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onEnabled(context: Context?) {
        Toast.makeText(context, "onEnabled called", Toast.LENGTH_LONG).show()
    }

    override fun onDisabled(context: Context?) {
        Toast.makeText(context, "onDisabled called", Toast.LENGTH_LONG).show()
    }

    companion object {
        fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {

            val views = RemoteViews(context.packageName, R.layout.list_widjet)
            setRemoteAdapter(context, views)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun setRemoteAdapter(context: Context, @NonNull views: RemoteViews) {
            views.setRemoteAdapter(
                R.id.widget_list,
                Intent(context, WidgetService::class.java)
            )
        }
    }
}