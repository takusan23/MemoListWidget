package io.github.takusan23.listwidget.Room.Init

import android.content.Context
import androidx.room.Room
import io.github.takusan23.listwidget.Room.DataBase.ListDB

/**
 * データベースを準備する関数
 * @param context こんてきすと
 * */
class InitListDB(context: Context) {
    val listDB = Room.databaseBuilder(context, ListDB::class.java, "ListDB.db").build()
}