package my.illrock.a1codechallenge.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MainType(
    val id: String,
    val name: String
) : Parcelable