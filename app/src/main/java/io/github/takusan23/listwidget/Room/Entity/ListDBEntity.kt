package io.github.takusan23.listwidget.Room.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * リストデータベースのEntity
 * @param content 内容
 * @param date 日付。UnixTimeにするには1000で割る
 * @param description サブテキスト的な
 * @param id 主キー
 * @param color カラーコード。省略時白
 * */
@Entity(tableName = "list")
data class ListDBEntity(
    @ColumnInfo(name = "_id") @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "color") val color: String = "#ffffff"
)
