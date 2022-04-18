package my.illrock.a1codechallenge.data.network

import my.illrock.a1codechallenge.data.network.response.manufacturer.BuiltDatesResponse
import my.illrock.a1codechallenge.data.network.response.manufacturer.MainTypesResponse
import my.illrock.a1codechallenge.data.network.response.manufacturer.ManufacturersResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("car-types/manufacturer")
    suspend fun getManufacturers(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): ManufacturersResponse

    @GET("car-types/main-types")
    suspend fun getMainTypes(
        @Query("manufacturer") manufacturerId: Long,
    ): MainTypesResponse

    @GET("car-types/built-dates")
    suspend fun getBuiltDates(
        @Query("manufacturer") manufacturerId: Long,
        @Query("main-type") mainTypeId: String
    ): BuiltDatesResponse
}