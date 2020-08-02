package io.github.takusan23.listwidget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.takusan23.listwidget.Adapter.ListRecyclerViewAdapter
import io.github.takusan23.listwidget.BottomFragment.NewItemBottomFragment
import io.github.takusan23.listwidget.Room.Entity.ListDBEntity
import io.github.takusan23.listwidget.Room.Init.InitListDB
import io.github.takusan23.listwidget.Widget.updateAppWidget
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * putExtraに
 * new | true | 起動と同時に作成画面を表示します
 * */
class MainActivity : AppCompatActivity() {

    // DBの中身配列
    val listDBList = arrayListOf<ListDBEntity>()
    val listAdapter = ListRecyclerViewAdapter(listDBList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // RecyclerView初期化
        initRecyclerView()

        GlobalScope.launch {
            // データ取得
            getItemFromDB()
        }

        // Fab押したときは追加画面
        activity_main_fab.setOnClickListener {
            showNewBottomFragment()
        }

        // ウィジェットの作成ボタンから来た
        if (intent.getBooleanExtra("new", false)) {
            showNewBottomFragment()
        }

    }

    /** 追加画面表示 */
    private fun showNewBottomFragment() {
        val newItemBottomFragment = NewItemBottomFragment()
        newItemBottomFragment.show(supportFragmentManager, "new")
    }

    /** データを取得する。コルーチンです */
    suspend fun getItemFromDB() = withContext(Dispatchers.IO) {
        // 消す
        listDBList.clear()
        // RecyclerView更新
        withContext(Dispatchers.Main) {
            listAdapter.notifyDataSetChanged()
        }
        // データベース
        val listDB = InitListDB(this@MainActivity).listDB
        // 日付順
        listDB.listDBDao().getItemFilter().sortedBy { listDBEntity -> listDBEntity.date }.forEach {
            listDBList.add(it)
        }
        // RecyclerView更新
        withContext(Dispatchers.Main) {
            listAdapter.notifyDataSetChanged()
            // ついでにホーム画面のウィジェットも更新
            updateAppWidget(this@MainActivity)
        }
    }

    /** RecyclerView初期化 */
    private fun initRecyclerView() {
        activity_main_recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = listAdapter
            val itemDecoration = DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL)
            addItemDecoration(itemDecoration)
        }
    }
}