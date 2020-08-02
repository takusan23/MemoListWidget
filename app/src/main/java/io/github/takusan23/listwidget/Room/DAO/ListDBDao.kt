package io.github.takusan23.listwidget.Room.DAO

import androidx.room.*
import io.github.takusan23.listwidget.Room.Entity.ListDBEntity

/**
 * データベースアクセス
 * */
@Dao
interface ListDBDao {
    /** 全データ取得 */
    @Query("SELECT * FROM list")
    fun getAll(): List<ListDBEntity>

    /** データ更新 */
    @Update
    fun update(listDBEntity: ListDBEntity)

    /** データ追加 */
    @Insert
    fun insert(listDBEntity: ListDBEntity)

    /** データ削除 */
    @Delete
    fun delete(listDBEntity: ListDBEntity)

    /** IDを使って削除する */
    @Query("DELETE FROM list WHERE _id = :id")
    fun deleteById(id: Int)

    /** 未来の日付の設定された値を取り出す。引数は省略していいよ */
    @Query("SELECT * FROM list WHERE date >= :date")
    fun getItemFilter(date: Long = System.currentTimeMillis() / 1000): List<ListDBEntity>

}
