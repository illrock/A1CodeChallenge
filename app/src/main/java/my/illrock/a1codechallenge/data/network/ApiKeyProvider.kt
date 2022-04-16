package my.illrock.a1codechallenge.data.network

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyProvider @Inject constructor() {
    fun get() = my.illrock.a1codechallenge.BuildConfig.API_KEY
}