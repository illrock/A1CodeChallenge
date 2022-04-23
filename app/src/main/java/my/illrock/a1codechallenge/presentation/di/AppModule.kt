package my.illrock.a1codechallenge.presentation.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import my.illrock.a1codechallenge.BuildConfig
import my.illrock.a1codechallenge.data.db.A1Database
import my.illrock.a1codechallenge.data.db.builtdates.BuiltDateDao
import my.illrock.a1codechallenge.data.db.maintypes.MainTypeDao
import my.illrock.a1codechallenge.data.network.ApiService
import my.illrock.a1codechallenge.data.network.interceptor.HttpInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideHttpClient(@ApplicationContext context: Context): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .addInterceptor(HttpInterceptor())
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
//        .addInterceptor(FakeInterceptor(context))
        .build()

    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .build()
    }

    @Provides
    fun provideApiService(httpClient: OkHttpClient, moshi: Moshi): ApiService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    fun provideCoroutineDispatcher() = Dispatchers.IO

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): A1Database {
        return Room.databaseBuilder(context, A1Database::class.java, A1Database.NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideMainTypeDao(database: A1Database): MainTypeDao = database.mainTypeDao()

    @Provides
    fun provideBuiltDateDao(database: A1Database): BuiltDateDao = database.builtDateDao()

    companion object {
        private const val CONNECT_TIMEOUT = 15L
        private const val READ_TIMEOUT = 70L
    }
}