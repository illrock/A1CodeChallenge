package my.illrock.a1codechallenge.data.db.maintypes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MainTypeDao {
    @Query("SELECT * FROM ${MainTypeEntity.TABLE_NAME} WHERE manufacturer_id = :manufacturerId")
    suspend fun getByManufacturerId(manufacturerId: Long): List<MainTypeEntity>

    @Query("DELETE FROM ${MainTypeEntity.TABLE_NAME} WHERE manufacturer_id = :manufacturerId")
    suspend fun deleteByManufacturerId(manufacturerId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mainTypes: List<MainTypeEntity>)
}