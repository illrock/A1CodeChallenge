package my.illrock.a1codechallenge.data.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MainTypesResponse(
    //todo useless paging params?
    val wkda: Map<String, String>
)