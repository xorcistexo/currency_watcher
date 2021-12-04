package su.lalayan.currencywatcher.data.remote

import retrofit2.Response
import retrofit2.http.GET
import su.lalayan.currencywatcher.data.model.currency.RatesResponse
import su.lalayan.currencywatcher.data.model.currency.SymbolsResponse

interface RatesApi {

    @GET("latest")
    suspend fun getRates(): Response<RatesResponse>

    @GET("symbols")
    suspend fun getSymbols(): Response<SymbolsResponse>
}
