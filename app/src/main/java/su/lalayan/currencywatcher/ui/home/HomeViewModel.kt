package su.lalayan.currencywatcher.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow.DROP_LATEST
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import su.lalayan.currencywatcher.domain.RatesUseCase
import su.lalayan.currencywatcher.data.model.currency.RatesResponse
import su.lalayan.currencywatcher.utils.network.common.NetworkResult
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val ratesUseCase: RatesUseCase
) : ViewModel() {

    private val state = MutableStateFlow<HomeFragmentState>(HomeFragmentState.Init)
    val mState: StateFlow<HomeFragmentState> get() = state.asStateFlow()

    private val _ratesResponse =
        MutableSharedFlow<RatesResponse?>(replay = 1, onBufferOverflow = DROP_LATEST)
    val ratesResponse: SharedFlow<RatesResponse?> = _ratesResponse.asSharedFlow()

    init {
        fetchCurrencyList()
    }

    fun fetchCurrencyList() {
        viewModelScope.launch {
            ratesUseCase.getRates()
                .onStart {
                    setLoading()
                }
                .catch { exception ->
                    hideLoading()
                    showToast(exception.message.toString())
                }
                .collect { result ->
                    hideLoading()
                    when (result) {
                        is NetworkResult.Success -> _ratesResponse.emit(result.data)
                        is NetworkResult.Error -> result.message?.let { showToast(it) }
                    }
                }
        }
    }

    private fun setLoading() {
        state.value = HomeFragmentState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = HomeFragmentState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = HomeFragmentState.ShowToast(message)
    }

}

sealed class HomeFragmentState {
    object Init : HomeFragmentState()
    data class IsLoading(val isLoading: Boolean) : HomeFragmentState()
    data class ShowToast(val message: String) : HomeFragmentState()
}