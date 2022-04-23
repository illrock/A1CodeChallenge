package my.illrock.a1codechallenge.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import my.illrock.a1codechallenge.data.db.builtdates.BuiltDateEntity

@Keep
@Parcelize
data class BuiltDate(
    val id: String,
    val date: String
) : Parcelable {
    constructor(entity: BuiltDateEntity) : this(entity.id, entity.date)
}