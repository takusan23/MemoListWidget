package io.github.takusan23.listwidget.Room.DataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.takusan23.listwidget.Room.DAO.ListDBDao
import io.github.takusan23.listwidget.Room.Entity.ListDBEntity

/**
 * データベース
 * */
@Database(entities = [ListDBEntity::class], version = 1)
abstract class ListDB : RoomDatabase() {
    abstract fun listDBDao(): ListDBDao
}
