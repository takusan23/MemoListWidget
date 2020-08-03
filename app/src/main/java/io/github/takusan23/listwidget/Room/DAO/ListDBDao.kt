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

    /** IDを使ってデータベースから取得する */
    @Query("SELECT * FROM list WHERE _id = :id")
    fun findById(id: Int): ListDBEntity?

    /** 未来の日付の設定された値を取り出す。引数は省略していいよ */
    @Query("SELECT * FROM list WHERE date >= :date")
    fun getItemFilter(date: Long = System.currentTimeMillis()): List<ListDBEntity>

    /** カテゴリを指定した [getItemFilter] */
    @Query("SELECT * FROM list WHERE date >= :date AND category = :category")
    fun getItemCategoryFilter(category: String, date: Long = System.currentTimeMillis()): List<ListDBEntity>

    /** カテゴリだけを取り出す。SELECT DISTINCT で重複消せる */
    @Query("SELECT DISTINCT category FROM list ")
    fun getCategoryList(): List<String>

}
