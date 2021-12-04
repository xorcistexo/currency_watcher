package su.lalayan.currencywatcher.data.remote

import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val api: RatesApi) {
    suspend fun getRates() = api.getRates()

    suspend fun getSymbols() = api.getSymbols()
}