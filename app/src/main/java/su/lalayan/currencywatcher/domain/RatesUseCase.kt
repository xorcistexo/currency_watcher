package su.lalayan.currencywatcher.domain

import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import su.lalayan.currencywatcher.data.remote.RemoteDataSource
import su.lalayan.currencywatcher.data.model.BaseApiResponse
import javax.inject.Inject

@ActivityRetainedScoped
class RatesUseCase @Inject constructor(private val source: RemoteDataSource) :
    BaseApiResponse() {

    suspend fun getRates() = flow {
        emit(safeApiCall { source.getRates() })
    }.flowOn(Dispatchers.IO)

    suspend fun getSymbols() = flow {
        emit(safeApiCall { source.getSymbols() })
    }.flowOn(Dispatchers.IO)
}

