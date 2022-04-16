package my.illrock.a1codechallenge.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import my.illrock.a1codechallenge.data.network.ApiKeyProvider
import my.illrock.a1codechallenge.data.network.ApiService
import my.illrock.a1codechallenge.data.network.response.ResultWrapper
import my.illrock.a1codechallenge.data.network.response.manufacturer.ManufacturersResponse
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManufacturerRepository @Inject constructor(
    private val apiService: ApiService,
    private val apiKeyProvider: ApiKeyProvider,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend fun get(page: Int, pageSize: Int): ResultWrapper<ManufacturersResponse> = withContext(dispatcher) {
        getFromNetwork(page, pageSize)
    }

    private suspend fun getFromNetwork(page: Int, pageSize: Int): ResultWrapper<ManufacturersResponse> {
        return try {
            val response = apiService.getManufacturers(apiKeyProvider.get(), page, pageSize)
            ResultWrapper.Success(response)
        } catch (e: Exception) {
            ResultWrapper.Error(e)
        }
    }
}