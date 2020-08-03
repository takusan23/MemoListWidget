package io.github.takusan23.listwidget.Adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.github.takusan23.listwidget.BottomFragment.NewItemBottomFragment
import io.github.takusan23.listwidget.MainActivity
import io.github.takusan23.listwidget.R
import io.github.takusan23.listwidget.Room.Entity.ListDBEntity
import io.github.takusan23.listwidget.Room.Init.InitListDB
import io.github.takusan23.listwidget.Tool.toFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text

/**
 * 一覧表示で使うAdapter
 * */
class ListRecyclerViewAdapter(private val arrayList: ArrayList<ListDBEntity>) :
    RecyclerView.Adapter<ListRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            val context = contentTextView.context
            // できすと表示
            val item = arrayList[position]
            linearLayout.background = ColorDrawable(Color.parseColor(item.color))
            contentTextView.text = item.content
            descriptionTextView.text = item.description
            categoryTextView.text = item.category
            dateTextView.text = item.date.toFormat() // 拡張関数
            // 削除ボタン
            deleteImageView.setOnClickListener {
                Snackbar.make(it, "削除していい？", Snackbar.LENGTH_SHORT).setAction("削除") {
                    // 削除する
                    GlobalScope.launch(Dispatchers.Main) {
                        val listDB = InitListDB(context).listDB
                        // 削除
                        withContext(Dispatchers.IO) {
                            listDB.listDBDao().deleteById(item.id)
                        }
                        // 更新する
                        (context as MainActivity).getItemFromDB()
                    }
                }.show()
            }
            // 編集ボタン
            editButton.setOnClickListener {
                val newItemBottomFragment = NewItemBottomFragment()
                val bundle = Bundle()
                bundle.putInt("id", item.id)
                newItemBottomFragment.arguments = bundle
                newItemBottomFragment.show((context as AppCompatActivity).supportFragmentManager, "edit")
            }
            // コピーボタン
            copyButton.setOnClickListener {
                // 値取り出す
                GlobalScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.IO) {
                        val dao = InitListDB(context).listDB.listDBDao()
                        val dbItem = dao.findById(item.id) ?: return@withContext
                        // 複製して入れる
                        dao.insert(dbItem.copy(id = 0))
                        // 更新かける
                        (context as MainActivity).getItemFromDB()
                    }
                }
            }
        }
    }

    override fun getItemCount() = arrayList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contentTextView = itemView.findViewById<TextView>(R.id.adapter_list_content_textview)
        val descriptionTextView = itemView.findViewById<TextView>(R.id.adapter_list_description_textview)
        val dateTextView = itemView.findViewById<TextView>(R.id.adapter_list_date_textview)
        val categoryTextView = itemView.findViewById<TextView>(R.id.adapter_list_date_category)
        val deleteImageView = itemView.findViewById<ImageView>(R.id.adapter_list_delete_button)
        val editButton = itemView.findViewById<ImageView>(R.id.adapter_list_edit_button)
        val copyButton = itemView.findViewById<ImageView>(R.id.adapter_list_copy_button)
        val linearLayout = itemView.findViewById<LinearLayout>(R.id.adapter_list_parent)
    }

}
