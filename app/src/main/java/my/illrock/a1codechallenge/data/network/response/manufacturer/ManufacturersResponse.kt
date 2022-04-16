package my.illrock.a1codechallenge.data.network.response.manufacturer

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ManufacturersResponse(
    val page: Int,
    val pageSize: Int,
    val totalPageCount: Int,
    val wkda: Map<Long, String>
)