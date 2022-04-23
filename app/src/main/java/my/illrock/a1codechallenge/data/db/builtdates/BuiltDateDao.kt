package my.illrock.a1codechallenge.data.db.builtdates

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BuiltDateDao {
    @Query("SELECT * FROM ${BuiltDateEntity.TABLE_NAME} WHERE manufacturer_id = :manufacturerId AND main_type_id = :mainTypeId")
    suspend fun getByManufacturerAndMainTypeIds(manufacturerId: Long, mainTypeId: String): List<BuiltDateEntity>

    @Query("DELETE FROM ${BuiltDateEntity.TABLE_NAME} WHERE manufacturer_id = :manufacturerId AND main_type_id = :mainTypeId")
    suspend fun deleteByManufacturerAndMainTypeIds(manufacturerId: Long, mainTypeId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(builtDates: List<BuiltDateEntity>)
}