package my.illrock.a1codechallenge.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class MainType(
    val id: String,
    val name: String
) : Parcelable