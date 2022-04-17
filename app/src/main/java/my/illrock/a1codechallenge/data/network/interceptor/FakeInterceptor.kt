package my.illrock.a1codechallenge.data.network.interceptor

import android.content.Context
import my.illrock.a1codechallenge.BuildConfig
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Very handy to test app with local raw json response.
 * Just add this interceptor to the end of your OkHttpClient.Builder() chain
 * */
class FakeInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (BuildConfig.DEBUG && chain.request().url.encodedPath.contains("/car-types/manufacturer")) {

            val inputStream = context.resources.openRawResource(0 /*R.raw.your_json*/)
            val response = BufferedReader(InputStreamReader(inputStream)).use(BufferedReader::readText)

            Response.Builder()
                .code(200)
                .message(response)
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .body(response.toResponseBody("application/json".toMediaTypeOrNull()))
                .addHeader("content-type", "application/json")
                .build()
        } else {
            chain.proceed(chain.request())
        }
    }
}