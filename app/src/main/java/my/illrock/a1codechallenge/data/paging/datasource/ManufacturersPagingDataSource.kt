package my.illrock.a1codechallenge.data.paging.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import my.illrock.a1codechallenge.data.model.Manufacturer
import my.illrock.a1codechallenge.data.network.ApiService
import my.illrock.a1codechallenge.data.repository.ManufacturersRepository
import java.lang.Exception

class ManufacturersPagingDataSource(
    private val apiService: ApiService
): PagingSource<Int, Manufacturer>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Manufacturer> {
        val pageNumber = params.key ?: DEFAULT_PAGE
        return try {
            val response = apiService
                .getManufacturers(pageNumber, ManufacturersRepository.PAGE_SIZE)
            val manufacturers = response.wkda
                .map { Manufacturer(it.key, it.value) }
            val prevKey = (response.page - 1)
                .takeIf { it >= 0 }
            val nextKey = (response.page + 1)
                .takeIf { it < response.totalPageCount }
            LoadResult.Page(
                data = manufacturers,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Manufacturer>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    companion object {
        private const val DEFAULT_PAGE = 0
    }
}