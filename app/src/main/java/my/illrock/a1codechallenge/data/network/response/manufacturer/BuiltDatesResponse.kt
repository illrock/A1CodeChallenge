package my.illrock.a1codechallenge.data.network.response.manufacturer

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BuiltDatesResponse(
    val wkda: Map<String, String>
)