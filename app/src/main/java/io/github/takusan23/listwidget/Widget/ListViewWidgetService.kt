package io.github.takusan23.listwidget.Widget

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import android.view.View
import android.widget.LinearLayout
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toBitmap
import androidx.preference.PreferenceManager
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

        var widgetListItem = arrayListOf<WidgetListItemData>()

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
            // 何を表示すれば良いのか
            val prefSetting = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val widgetCategory = prefSetting.getString("widget_category", "") ?: ""
            if (widgetCategory == "") {
                // カテゴリ一覧
                runBlocking {
                    widgetListItem.clear()
                    val dao = InitListDB(applicationContext).listDB.listDBDao()
                    dao.getCategoryList().forEach {
                        widgetListItem.add(WidgetListItemData(it, null))
                    }
                }
            } else {
                // カテゴリ指定
                runBlocking {
                    widgetListItem.clear()
                    widgetListItem.add(WidgetListItemData("戻る", null))
                    val dao = InitListDB(applicationContext).listDB.listDBDao()
                    dao.getItemCategoryFilter(widgetCategory).sortedBy { listDBEntity -> listDBEntity.date }.forEach {
                        widgetListItem.add(WidgetListItemData("", it))
                    }
                }
            }
        }

        override fun hasStableIds(): Boolean {
            return true
        }

        override fun getViewAt(p0: Int): RemoteViews {
            // 値
            val item = widgetListItem[p0]
            val remoteViews = RemoteViews(packageName, R.layout.listview_item_layout)
            if (item.listDBEntity != null) {
                val entity = item.listDBEntity
                // ListViewにセットする
                remoteViews.apply {
                    setViewVisibility(R.id.widget_listview_item_description_textview, View.VISIBLE)
                    setViewVisibility(R.id.widget_listview_item_date_textview, View.VISIBLE)
                    setImageViewBitmap(R.id.widget_listview_item_background_imageview, ColorDrawable(Color.parseColor(entity.color)).toBitmap(10, 10))
                    setTextViewText(R.id.widget_listview_item_content_textview, entity.content)
                    setTextViewText(R.id.widget_listview_item_description_textview, entity.description)
                    setTextViewText(R.id.widget_listview_item_date_textview, entity.date.toFormat())
                    // TextView前のアイコン消す
                    setTextViewCompoundDrawables(R.id.widget_listview_item_content_textview, 0, 0, 0, 0)
                    // クリックも消す
                    setOnClickFillInIntent(R.id.widget_listview_item_content_textview, null)
                }
            } else {
                // カテゴリ一覧と戻る表示
                remoteViews.apply {
                    setImageViewBitmap(R.id.widget_listview_item_background_imageview, null)
                    setTextViewText(R.id.widget_listview_item_content_textview, item.title)
                    setViewVisibility(R.id.widget_listview_item_description_textview, View.GONE)
                    setViewVisibility(R.id.widget_listview_item_date_textview, View.GONE)
                    // TextViewの前のアイコン消す
                    setTextViewCompoundDrawables(R.id.widget_listview_item_content_textview, 0, 0, 0, 0)
                    // カテゴリ切り替え
                    if (item.title != "戻る") {
                        // アイコン出す
                        val intent = Intent(applicationContext, ListWidget::class.java)
                        intent.putExtra("category", item.title)
                        setOnClickFillInIntent(R.id.widget_listview_item_content_textview, intent)
                        setTextViewCompoundDrawables(R.id.widget_listview_item_content_textview, R.drawable.ic_local_offer_black_24dp, 0, 0, 0)
                    } else {
                        // 戻る
                        val intent = Intent(applicationContext, ListWidget::class.java)
                        intent.putExtra("back", true)
                        setOnClickFillInIntent(R.id.widget_listview_item_content_textview, intent)
                        setTextViewCompoundDrawables(R.id.widget_listview_item_content_textview, R.drawable.ic_outline_arrow_back_24, 0, 0, 0)
                    }
                }
            }
            return remoteViews
        }

        override fun getCount(): Int {
            return widgetListItem.size
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun onDestroy() {

        }

    }

    /**
     * WidgetのListViewに表示するデータ
     * @param title カテゴリ一覧表示で使う。listDBEntityを入れてる場合は使わない
     * @param listDBEntity あれば表示。
     * */
    data class WidgetListItemData(
        val title: String,
        val listDBEntity: ListDBEntity?
    )

}
