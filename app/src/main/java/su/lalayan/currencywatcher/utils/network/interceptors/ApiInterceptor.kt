package su.lalayan.currencywatcher.utils.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import su.lalayan.currencywatcher.utils.common.Constants.Companion.KEY_API_KEY
import su.lalayan.currencywatcher.utils.common.Constants.Companion.KEY_BASE_CURRENCY
import su.lalayan.currencywatcher.utils.prefs.utils.SharedPrefs

class ApiInterceptor(private val apiKey: String, private val sharedPrefs: SharedPrefs) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalHttpUrl = originalRequest.url

        val url = originalHttpUrl.newBuilder()
            .addQueryParameter(KEY_BASE_CURRENCY, sharedPrefs.getDefaultCurrency())
            .addQueryParameter(KEY_API_KEY, apiKey)
            .build()

        val requestBuilder = originalRequest.newBuilder().url(url)

        val request = requestBuilder.build()
        return chain.proceed(request)
    }

}