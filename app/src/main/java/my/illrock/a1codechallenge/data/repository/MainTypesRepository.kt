package my.illrock.a1codechallenge.data.repository

import android.text.format.DateUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import my.illrock.a1codechallenge.data.db.maintypes.MainTypeDao
import my.illrock.a1codechallenge.data.db.maintypes.MainTypeEntity
import my.illrock.a1codechallenge.data.model.MainType
import my.illrock.a1codechallenge.data.network.ApiService
import my.illrock.a1codechallenge.data.network.response.ResultWrapper
import my.illrock.a1codechallenge.data.provider.SystemClockProvider
import my.illrock.a1codechallenge.data.repository.exception.NoDataException
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class MainTypesRepository @Inject constructor(
    private val apiService: ApiService,
    private val mainTypeDao: MainTypeDao,
    private val dispatcher: CoroutineDispatcher,
    private val systemClockProvider: SystemClockProvider,
) {
    suspend fun get(manufacturerId: Long, isForce: Boolean): ResultWrapper<List<MainType>> = withContext(dispatcher) {
        val result = if (isForce) {
            getFromNetwork(manufacturerId)
        } else {
            getFromDbOrNetwork(manufacturerId)
        }
        result.sort()
    }

    private suspend fun getFromNetwork(manufacturerId: Long): ResultWrapper<List<MainType>> {
        return try {
            mainTypeDao.deleteByManufacturerId(manufacturerId)

            val response = apiService.getMainTypes(manufacturerId)
            val mainTypes = response.wkda.map { MainType(it.key, it.value) }
            if (mainTypes.isEmpty()) {
                ResultWrapper.Error(NoDataException())
            } else {
                val time = systemClockProvider.elapsedRealtime()
                val entities = mainTypes.map { MainTypeEntity(it.id, it.name, manufacturerId, time) }
                mainTypeDao.insert(entities)
                ResultWrapper.Success(mainTypes)
            }
        } catch (e: Exception) {
            ResultWrapper.Error(e)
        }
    }

    private suspend fun getFromDbOrNetwork(manufacturerId: Long): ResultWrapper<List<MainType>> {
        val entities = getDbEntities(manufacturerId)
        return if (isOutdated(entities)) {
            getFromNetwork(manufacturerId)
        } else {
            getFromDb(entities)
        }
    }

    private suspend fun getDbEntities(manufacturerId: Long): List<MainTypeEntity> =
        mainTypeDao.getByManufacturerId(manufacturerId)

    private suspend fun getFromDb(entities: List<MainTypeEntity>) = ResultWrapper.Success(
        entities.map { MainType(it) }
    )

    private fun isOutdated(entities: List<MainTypeEntity>) = if (entities.isEmpty()) {
        true
    } else {
        val lastUpdate = entities[0].lastUpdateTime
        CACHE_LIFETIME < abs(systemClockProvider.elapsedRealtime() - lastUpdate)
    }

    private fun ResultWrapper<List<MainType>>.sort() = if (this is ResultWrapper.Success) {
        ResultWrapper.Success(data.sortedBy { it.name })
    } else {
        this
    }

    companion object {
        private const val CACHE_LIFETIME = DateUtils.HOUR_IN_MILLIS
    }
}