package io.github.takusan23.listwidget.BottomFragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import io.github.takusan23.listwidget.MainActivity
import io.github.takusan23.listwidget.R
import io.github.takusan23.listwidget.Room.Entity.ListDBEntity
import io.github.takusan23.listwidget.Room.Init.InitListDB
import io.github.takusan23.listwidget.Tool.toFormat
import kotlinx.android.synthetic.main.bottom_fragment_new_item.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * 追加BottomSheetFragment
 * 以下の値を入れると編集モードになります
 * id | Int | データベースの主キー
 * */
class NewItemBottomFragment : BottomSheetDialogFragment() {

    /** 時刻 */
    var calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    /** 追加するときの背景色 */
    var colorHexCode = "#ffffff"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_fragment_new_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 編集モードかもしれない
        if (arguments?.getInt("id") != null) {
            val dbId = arguments!!.getInt("id")
            // 値取り出し
            GlobalScope.launch(Dispatchers.Main) {
                val item = withContext(Dispatchers.IO) {
                    InitListDB(requireContext()).listDB.listDBDao().findById(dbId)
                } ?: return@launch // なければ終了
                bottom_fragment_new_item_content.setText(item.content)
                bottom_fragment_new_item_description.setText(item.description)
                bottom_fragment_new_item_category.setText(item.category)
                bottom_fragment_new_item_color.chipIconTint = ColorStateList.valueOf(Color.parseColor(item.color))
                // いろ
                colorHexCode = item.color
                // 時間
                calendar.timeInMillis = item.date
                bottom_fragment_new_item_calendar.text = setDateFormat(item.date)
                bottom_fragment_new_item_time.text = setTimeFormat(item.date)
            }
        }

        bottom_fragment_new_item_button.setOnClickListener {
            // データベース追加
            GlobalScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {
                    val content = bottom_fragment_new_item_content.text.toString()
                    val description = bottom_fragment_new_item_description.text.toString()
                    val category = bottom_fragment_new_item_category.text.toString()
                    // 内容。更新と分岐
                    if (arguments?.getInt("id") != null) {
                        // 更新
                        val data = ListDBEntity(
                            content = content,
                            description = description,
                            date = calendar.time.time,
                            color = colorHexCode,
                            id = arguments!!.getInt("id"),
                            category = category
                        )
                        InitListDB(requireContext()).listDB.listDBDao().update(data)
                    } else {
                        // 新規登録
                        val data = ListDBEntity(
                            content = content,
                            description = description,
                            date = calendar.time.time,
                            color = colorHexCode,
                            category = category
                        )
                        InitListDB(requireContext()).listDB.listDBDao().insert(data)
                    }
                }
                Toast.makeText(context, "追加(更新)しました", Toast.LENGTH_SHORT).show()
                dismiss()
                // 更新
                (activity as MainActivity).getItemFromDB()
            }
        }

        // 日付
        bottom_fragment_new_item_calendar.setOnClickListener {
            // （悪名高い）時間ピッカー
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val datePickerDialog = DatePickerDialog(requireContext())
                // 選択したとき
                datePickerDialog.setOnDateSetListener { datePicker, i, i2, i3 ->
                    // 時間指定
                    calendar.set(i, i2, i3)
                    bottom_fragment_new_item_calendar.text = "日付：$i/${i2 + 1}/$i3"
                }
                // 表示
                datePickerDialog.show()
            }
        }

        // 色
        bottom_fragment_new_item_color.setOnClickListener {
            // からーぴっかー
            val colorDialog = ColorPickerDialog.newBuilder().create()
            // 色決定
            colorDialog.setColorPickerDialogListener(object : ColorPickerDialogListener {
                override fun onDialogDismissed(dialogId: Int) {

                }

                override fun onColorSelected(dialogId: Int, color: Int) {
                    // https://stackoverflow.com/questions/6539879/how-to-convert-a-color-integer-to-a-hex-string-in-android
                    val hexColor = String.format("#%06X", 0xFFFFFF and color)
                    bottom_fragment_new_item_color.chipIconTint = ColorStateList.valueOf(color)
                    colorHexCode = hexColor
                }
            })
            colorDialog.show(childFragmentManager, "color")
        }

        // 時間
        bottom_fragment_new_item_time.setOnClickListener {
            println(setTimeFormat(calendar.time.time))
            val timePickerDialog = TimePickerDialog(context!!, TimePickerDialog.OnTimeSetListener { p0, p1, p2 ->
                // 選択時
                calendar.set(Calendar.HOUR_OF_DAY, p1)
                calendar.set(Calendar.MINUTE, p2)
                bottom_fragment_new_item_time.text = "時刻：$p1:$p2"
            }, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], true)
            timePickerDialog.show()
        }

    }

    /** DBから取り出した値を時間 */
    private fun setTimeFormat(date: Long): String {
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]
        return "時刻：$hour:$minute"
    }

    /** DBから取り出した値をきれいにする */
    private fun setDateFormat(date: Long): String {
        val year = calendar[Calendar.YEAR]
        val day = calendar[Calendar.DAY_OF_MONTH]
        val month = calendar[Calendar.MONTH] + 1
        return "日付：$year/$month/$day"
    }

}