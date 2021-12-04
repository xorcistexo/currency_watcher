package su.lalayan.currencywatcher.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import su.lalayan.currencywatcher.data.remote.RatesApi
import su.lalayan.currencywatcher.utils.common.Constants.Companion.API_KEY
import su.lalayan.currencywatcher.utils.common.Constants.Companion.BASE_URL
import su.lalayan.currencywatcher.utils.network.interceptors.ApiInterceptor
import su.lalayan.currencywatcher.utils.prefs.utils.SharedPrefs
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideHttpClient(
        apiInterceptor: ApiInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ) = OkHttpClient
        .Builder()
        .readTimeout(15, TimeUnit.SECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(apiInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun provideCurrencyService(retrofit: Retrofit): RatesApi =
        retrofit.create(RatesApi::class.java)

    @Singleton
    @Provides
    fun provideRequestInterceptor(prefs: SharedPrefs) = ApiInterceptor(API_KEY, prefs)

    @Singleton
    @Provides
    fun provideLoggingInterceptor() =
        HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
}