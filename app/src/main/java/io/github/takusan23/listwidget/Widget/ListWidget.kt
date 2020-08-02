package io.github.takusan23.listwidget.Widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import io.github.takusan23.listwidget.MainActivity
import io.github.takusan23.listwidget.R

/**
 * Implementation of App Widget functionality.
 */
class ListWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

/**
 * ウィジェット更新関数。ContextがあればActivityだろうとServiceだろうと更新可能。
 * @param context こんてきすと
 * */
internal fun updateAppWidget(context: Context) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.list_widget)
    // ListView
    val remoteViewsFactoryIntent = Intent(context, ListViewWidgetService::class.java)
    views.setRemoteAdapter(R.id.widget_listview, remoteViewsFactoryIntent)
    // アプリ起動
    val intent = Intent(context, MainActivity::class.java)
    intent.putExtra("new", true) // 追加画面表示
    val pendingIntent = PendingIntent.getActivity(context, 114, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    views.setOnClickPendingIntent(R.id.widget_add_item, pendingIntent)
    // Contextあれば更新できる！
    val componentName = ComponentName(context, ListWidget::class.java)
    val manager = AppWidgetManager.getInstance(context)
    val ids = manager.getAppWidgetIds(componentName)
    ids.forEach { widgetId ->
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(widgetId, R.id.widget_listview) // ListView更新
        manager.updateAppWidget(widgetId, views)
    }
}