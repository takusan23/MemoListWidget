package io.github.takusan23.listwidget.Tool

import java.text.SimpleDateFormat

/**
 * UnixTime（ミリ秒ではなく秒）をフォーマットして文字列に変換する 拡張関数
 * */
fun Long.toFormat(): String? {
    val simpleDateFormat = SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒")
    return simpleDateFormat.format(this)
}