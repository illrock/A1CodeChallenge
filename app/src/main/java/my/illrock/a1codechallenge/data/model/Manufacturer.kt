package my.illrock.a1codechallenge.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Manufacturer(
    val id: Long,
    val name: String
) : Parcelable