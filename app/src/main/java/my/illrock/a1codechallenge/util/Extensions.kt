package my.illrock.a1codechallenge.util

import android.content.res.Resources
import android.view.View
import my.illrock.a1codechallenge.BuildConfig
import my.illrock.a1codechallenge.R
import java.net.UnknownHostException

fun Throwable.print() = if (BuildConfig.DEBUG) printStackTrace() else Unit

fun View.switchBackgroundColor(position: Int) {
    val backgroundColorRes = if (position % 2 == 0) {
        R.color.item_background_first
    } else R.color.item_background_second
    setBackgroundResource(backgroundColorRes)
}

fun Throwable?.getErrorMessage(resources: Resources): String? = when (this) {
    is UnknownHostException -> resources.getString(R.string.error_connection)
    else -> this?.message
}