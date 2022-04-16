package my.illrock.a1codechallenge.util

import my.illrock.a1codechallenge.BuildConfig

fun Throwable.print() = if (BuildConfig.DEBUG) printStackTrace() else Unit