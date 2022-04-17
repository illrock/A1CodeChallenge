package my.illrock.a1codechallenge.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import my.illrock.a1codechallenge.data.model.Manufacturer
import my.illrock.a1codechallenge.data.network.ApiService
import my.illrock.a1codechallenge.data.paging.datasource.ManufacturersPagingDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManufacturersRepository @Inject constructor(
    private val apiService: ApiService
) {
    //todo use remote mediator, store results in Room db?
    suspend fun get(): Flow<PagingData<Manufacturer>> = Pager(
        config = PagingConfig(PAGE_SIZE, PREFETCH_DISTANCE),
        pagingSourceFactory = { ManufacturersPagingDataSource(apiService) }
    ).flow

    companion object {
        const val PAGE_SIZE = 15
        const val PREFETCH_DISTANCE = 10
    }
}