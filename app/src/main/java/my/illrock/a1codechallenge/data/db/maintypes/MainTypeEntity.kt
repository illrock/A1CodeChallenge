package my.illrock.a1codechallenge.data.db.maintypes

import android.provider.BaseColumns
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import my.illrock.a1codechallenge.data.db.maintypes.MainTypeEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class MainTypeEntity(
    @PrimaryKey
    @ColumnInfo(name = BaseColumns._ID)
    val id: String,
    @ColumnInfo(name = NAME)
    val name: String,
    @ColumnInfo(name = MANUFACTURER_ID)
    val manufacturerId: Long,
    @ColumnInfo(name = LAST_UPDATE_TIME)
    val lastUpdateTime: Long
) {
    companion object {
        const val TABLE_NAME = "main_type"
        const val NAME = "name"
        const val MANUFACTURER_ID = "manufacturer_id"
        const val LAST_UPDATE_TIME = "last_update_time"
    }
}