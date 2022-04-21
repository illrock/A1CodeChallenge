package my.illrock.a1codechallenge.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import my.illrock.a1codechallenge.data.model.BuiltDate
import my.illrock.a1codechallenge.data.network.ApiService
import my.illrock.a1codechallenge.data.network.response.ResultWrapper
import my.illrock.a1codechallenge.data.repository.exception.NoDataException
import java.lang.Exception
import javax.inject.Inject

class BuiltDatesRepository @Inject constructor(
    private val apiService: ApiService,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun get(manufacturerId: Long, mainType: String): ResultWrapper<List<BuiltDate>> = withContext(dispatcher) {
        getFromNetwork(manufacturerId, mainType)
    }

    private suspend fun getFromNetwork(manufacturerId: Long, mainType: String): ResultWrapper<List<BuiltDate>> {
        return try {
            val response = apiService.getBuiltDates(manufacturerId, mainType)
            val builtDates = response.wkda.map { BuiltDate(it.key, it.value) }
            if (builtDates.isEmpty()) ResultWrapper.Error(NoDataException())
            else ResultWrapper.Success(builtDates)
        } catch (e: Exception) {
            ResultWrapper.Error(e)
        }
    }
}