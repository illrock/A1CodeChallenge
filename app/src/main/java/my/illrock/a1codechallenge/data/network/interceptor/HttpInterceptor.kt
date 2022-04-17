package my.illrock.a1codechallenge.data.network.interceptor

import my.illrock.a1codechallenge.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class HttpInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val urlBuilder = request.url.newBuilder()
            .addEncodedQueryParameter(PARAM_WA_KEY, BuildConfig.API_KEY)
        val modifiedRequest = request.newBuilder()
            .url(urlBuilder.build())
            .build()
        return chain.proceed(modifiedRequest)
    }

    private companion object {
        const val PARAM_WA_KEY = "wa_key"
    }
}