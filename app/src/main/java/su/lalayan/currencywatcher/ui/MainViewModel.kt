package su.lalayan.currencywatcher.ui

import androidx.annotation.IdRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import su.lalayan.currencywatcher.R
import su.lalayan.currencywatcher.domain.RatesUseCase
import su.lalayan.currencywatcher.data.model.currency.SymbolsResponse
import su.lalayan.currencywatcher.utils.network.common.NetworkResult
import su.lalayan.currencywatcher.utils.prefs.utils.SharedPrefs
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val ratesUseCase: RatesUseCase,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    private val _defaultCurrency = MutableStateFlow(sharedPrefs.getDefaultCurrency())
    val defaultCurrency: StateFlow<String> get() = _defaultCurrency.asStateFlow()

    private val _popUpState =
        MutableStateFlow<FilterPopUpState>(FilterPopUpState.AtoZ)
    val popUpState: StateFlow<FilterPopUpState> get() = _popUpState.asStateFlow()

    private val _stateCurrencySpinner =
        MutableStateFlow<CurrencySpinnerState>(CurrencySpinnerState.Init)
    val stateCurrencySpinner: StateFlow<CurrencySpinnerState> get() = _stateCurrencySpinner.asStateFlow()

    private val _symbolsResponse =
        MutableSharedFlow<SymbolsResponse?>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_LATEST
        )
    val symbolsResponse: SharedFlow<SymbolsResponse?> = _symbolsResponse.asSharedFlow()

    init {
        fetchSymbols()
    }

    fun setDefaultCurrency(currency: String) {
        sharedPrefs.saveDefaultCurrency(currency)
        _defaultCurrency.tryEmit(currency)
    }

    private fun fetchSymbols() {
        viewModelScope.launch {
            ratesUseCase.getSymbols()
                .catch { exception ->
                    showToast(exception.message.toString())
                }
                .collect { result ->
                    when (result) {
                        is NetworkResult.Success -> _symbolsResponse.emit(result.data)
                        is NetworkResult.Error -> result.message?.let { showToast(it) }
                    }
                }
        }
    }

    private fun showToast(message: String) {
        _stateCurrencySpinner.value = CurrencySpinnerState.ShowToast(message)
    }

    fun setPopUpState(@IdRes menuId: Int) {
        when (menuId) {
            R.id.a_to_z -> _popUpState.value = FilterPopUpState.AtoZ
            R.id.z_to_a -> _popUpState.value = FilterPopUpState.ZtoA
            R.id.by_value_descending -> _popUpState.value = FilterPopUpState.ByValueDescending
            R.id.by_value_ascending -> _popUpState.value = FilterPopUpState.ByValueAscending
        }
    }
}

sealed class CurrencySpinnerState {
    object Init : CurrencySpinnerState()
    data class ShowToast(val message: String) : CurrencySpinnerState()
}

sealed class FilterPopUpState {
    object AtoZ : FilterPopUpState()
    object ZtoA : FilterPopUpState()
    object ByValueDescending : FilterPopUpState()
    object ByValueAscending : FilterPopUpState()
}