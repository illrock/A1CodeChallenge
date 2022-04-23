package my.illrock.a1codechallenge.data.repository

import android.text.format.DateUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import my.illrock.a1codechallenge.data.db.builtdates.BuiltDateDao
import my.illrock.a1codechallenge.data.db.builtdates.BuiltDateEntity
import my.illrock.a1codechallenge.data.model.BuiltDate
import my.illrock.a1codechallenge.data.network.ApiService
import my.illrock.a1codechallenge.data.network.response.ResultWrapper
import my.illrock.a1codechallenge.data.provider.SystemClockProvider
import my.illrock.a1codechallenge.data.repository.exception.NoDataException
import java.lang.Exception
import javax.inject.Inject
import kotlin.math.abs

class BuiltDatesRepository @Inject constructor(
    private val apiService: ApiService,
    private val builtDateDao: BuiltDateDao,
    private val dispatcher: CoroutineDispatcher,
    private val systemClockProvider: SystemClockProvider
) {
    suspend fun get(manufacturerId: Long, mainTypeId: String, isForce: Boolean): ResultWrapper<List<BuiltDate>> = withContext(dispatcher) {
        val result = if (isForce) {
            getFromNetwork(manufacturerId, mainTypeId)
        } else {
            getFromDbOrNetwork(manufacturerId, mainTypeId)
        }
        result.sort()
    }

    private suspend fun getFromNetwork(manufacturerId: Long, mainTypeId: String): ResultWrapper<List<BuiltDate>> {
        return try {
            builtDateDao.deleteByManufacturerAndMainTypeIds(manufacturerId, mainTypeId)

            val response = apiService.getBuiltDates(manufacturerId, mainTypeId)
            val builtDates = response.wkda.map { BuiltDate(it.key, it.value) }
            if (builtDates.isEmpty()) {
                ResultWrapper.Error(NoDataException())
            } else {
                val time = systemClockProvider.elapsedRealtime()
                val entities = builtDates.map { BuiltDateEntity(it.id, it.date, manufacturerId, mainTypeId, time) }
                builtDateDao.insert(entities)
                ResultWrapper.Success(builtDates)
            }
        } catch (e: Exception) {
            ResultWrapper.Error(e)
        }
    }

    private suspend fun getFromDbOrNetwork(manufacturerId: Long, mainTypeId: String): ResultWrapper<List<BuiltDate>> {
        val entities = getDbEntities(manufacturerId, mainTypeId)
        return if (isOutdated(entities)) {
            getFromNetwork(manufacturerId, mainTypeId)
        } else {
            getFromDb(entities)
        }
    }

    private suspend fun getDbEntities(manufacturerId: Long, mainTypeId: String): List<BuiltDateEntity> =
        builtDateDao.getByManufacturerAndMainTypeIds(manufacturerId, mainTypeId)

    private suspend fun getFromDb(entities: List<BuiltDateEntity>) = ResultWrapper.Success(
        entities.map { BuiltDate(it) }
    )

    private fun isOutdated(entities: List<BuiltDateEntity>) = if (entities.isEmpty()) {
        true
    } else {
        val lastUpdate = entities[0].lastUpdateTime
        CACHE_LIFETIME < abs(systemClockProvider.elapsedRealtime() - lastUpdate)
    }

    private fun ResultWrapper<List<BuiltDate>>.sort() = if (this is ResultWrapper.Success) {
        ResultWrapper.Success(data.sortedByDescending { it.date })
    } else {
        this
    }

    companion object {
        private const val CACHE_LIFETIME = DateUtils.HOUR_IN_MILLIS
    }
}