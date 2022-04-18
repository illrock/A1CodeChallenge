package my.illrock.a1codechallenge.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import my.illrock.a1codechallenge.data.model.MainType
import my.illrock.a1codechallenge.data.network.ApiService
import my.illrock.a1codechallenge.data.network.response.ResultWrapper
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainTypesRepository @Inject constructor(
    private val apiService: ApiService,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun get(manufacturerId: Long): ResultWrapper<List<MainType>> = withContext(dispatcher) {
        getFromNetwork(manufacturerId)
    }

    private suspend fun getFromNetwork(manufacturerId: Long): ResultWrapper<List<MainType>> {
        return try {
            val response = apiService.getMainTypes(manufacturerId)
            val mainTypesList = response.wkda.map { MainType(it.key, it.value) }
            ResultWrapper.Success(mainTypesList)
        } catch (e: Exception) {
            ResultWrapper.Error(e)
        }
    }
}