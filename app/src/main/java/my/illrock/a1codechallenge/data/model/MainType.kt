package my.illrock.a1codechallenge.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import my.illrock.a1codechallenge.data.db.maintypes.MainTypeEntity

@Keep
@Parcelize
data class MainType(
    val id: String,
    val name: String
) : Parcelable {
    constructor(entity: MainTypeEntity) : this(entity.id, entity.name)
}