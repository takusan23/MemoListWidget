package io.github.takusan23.listwidget.Widget

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.LinearLayout
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toBitmap
import io.github.takusan23.listwidget.R
import io.github.takusan23.listwidget.Room.Entity.ListDBEntity
import io.github.takusan23.listwidget.Room.Init.InitListDB
import io.github.takusan23.listwidget.Tool.toFormat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * WidgetのListViewのAdapter的ななにか。
 * AndroidManifestに書かないと動かないです。
 * */
class ListViewWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(p0: Intent?): RemoteViewsFactory {
        return ListViewWidgetFactory()
    }

    private inner class ListViewWidgetFactory : RemoteViewsFactory {

        // 取得したデータ
        var itemList = arrayListOf<ListDBEntity>()

        override fun onCreate() {

        }

        override fun getLoadingView(): RemoteViews? {
            return null
        }

        override fun getItemId(p0: Int): Long {
            return 0
        }

        override fun onDataSetChanged() {
            // データ取得を行う
            runBlocking {
                itemList.clear()
                val dao = InitListDB(applicationContext).listDB.listDBDao()
                dao.getItemFilter().sortedBy { listDBEntity -> listDBEntity.date }.forEach {
                    itemList.add(it)
                }
            }
        }

        override fun hasStableIds(): Boolean {
            return true
        }

        override fun getViewAt(p0: Int): RemoteViews {
            // 値
            val item = itemList[p0]
            // ListViewにセットする
            val remoteViews = RemoteViews(packageName, R.layout.listview_item_layout)
            remoteViews.setImageViewBitmap(R.id.widget_listview_item_background_imageview, ColorDrawable(Color.parseColor(item.color)).toBitmap(10, 10))
            remoteViews.setTextViewText(R.id.widget_listview_item_content_textview, item.content)
            remoteViews.setTextViewText(R.id.widget_listview_item_description_textview, item.description)
            remoteViews.setTextViewText(R.id.widget_listview_item_date_textview, item.date.toFormat())
            return remoteViews
        }

        override fun getCount(): Int {
            return itemList.size
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun onDestroy() {

        }

    }
}
