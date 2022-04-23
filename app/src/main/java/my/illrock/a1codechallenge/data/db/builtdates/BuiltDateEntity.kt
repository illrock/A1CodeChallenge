package my.illrock.a1codechallenge.data.db.builtdates

import android.provider.BaseColumns
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import my.illrock.a1codechallenge.data.db.builtdates.BuiltDateEntity.Companion.MAIN_TYPE_ID
import my.illrock.a1codechallenge.data.db.builtdates.BuiltDateEntity.Companion.TABLE_NAME
import my.illrock.a1codechallenge.data.db.maintypes.MainTypeEntity

@Entity(
    tableName = TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = MainTypeEntity::class,
            parentColumns = [BaseColumns._ID],
            childColumns = [MAIN_TYPE_ID],
            onDelete = CASCADE,
            deferred = true
        )
    ]
)
data class BuiltDateEntity(
    @PrimaryKey
    @ColumnInfo(name = BaseColumns._ID)
    val id: String,
    @ColumnInfo(name = DATE)
    val date: String,
    @ColumnInfo(name = MANUFACTURER_ID)
    val manufacturerId: Long,
    @ColumnInfo(name = MAIN_TYPE_ID)
    val mainTypeId: String,
    @ColumnInfo(name = LAST_UPDATE_TIME)
    val lastUpdateTime: Long
) {
    companion object {
        const val TABLE_NAME = "built_date"
        const val DATE = "date"
        const val MANUFACTURER_ID = "manufacturer_id"
        const val MAIN_TYPE_ID = "main_type_id"
        const val LAST_UPDATE_TIME = "last_update_time"
    }
}