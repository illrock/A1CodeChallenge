package my.illrock.a1codechallenge.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.inputmethod.InputMethodManager
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

fun Context.hideKeyboardFrom(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.showKeyboard(input: View) {
    input.requestFocus()
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT)
}