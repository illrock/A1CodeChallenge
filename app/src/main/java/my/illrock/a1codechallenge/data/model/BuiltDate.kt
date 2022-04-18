package my.illrock.a1codechallenge.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class BuiltDate(
    val id: String,
    val date: String
) : Parcelable