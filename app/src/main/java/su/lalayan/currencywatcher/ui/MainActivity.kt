package su.lalayan.currencywatcher.ui

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import su.lalayan.currencywatcher.R
import su.lalayan.currencywatcher.databinding.ActivityMainBinding
import su.lalayan.currencywatcher.data.model.currency.SymbolsResponse
import su.lalayan.currencywatcher.utils.ext.showToast

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    private lateinit var binding: ActivityMainBinding
    private lateinit var listPopupWindow: ListPopupWindow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initUI()
        initListeners()
        observe()
    }

    private fun initUI() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)
    }

    private fun initListeners() {
        binding.toolbarMain.txtBaseCurrency.setOnClickListener {
            listPopupWindow.show()
        }

        binding.toolbarMain.imgFilter.setOnClickListener { view ->
            showMenu(view, R.menu.filter_pop_up_menu)
        }
    }

    private fun observe() {
        observeDefaultCurrency()
        observeState()
        observeSymbols()
    }

    private fun observeDefaultCurrency() {
        viewModel.defaultCurrency
            .flowWithLifecycle(lifecycle)
            .onEach { currency ->
                handleDefaultCurrency(currency)
            }
            .launchIn(lifecycleScope)
    }

    private fun observeState() {
        viewModel.stateCurrencySpinner
            .flowWithLifecycle(lifecycle)
            .onEach { state ->
                handleState(state)
            }
            .launchIn(lifecycleScope)
    }

    private fun observeSymbols() {
        viewModel.symbolsResponse
            .flowWithLifecycle(lifecycle)
            .onEach { ratesResponse ->
                ratesResponse?.let { response ->
                    handleSymbols(response)
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun handleDefaultCurrency(currency: String) {
        binding.toolbarMain.txtBaseCurrency.text = currency
    }

    private fun handleState(currencySpinnerState: CurrencySpinnerState) {
        when (currencySpinnerState) {
            is CurrencySpinnerState.ShowToast -> showToast(currencySpinnerState.message)
            is CurrencySpinnerState.Init -> Unit
        }
    }

    private fun handleSymbols(response: SymbolsResponse) {
        initPopUp(response.symbols.keys)
    }

    private fun initPopUp(items: Set<String>) {
        listPopupWindow = ListPopupWindow(this, null, R.attr.listPopupWindowStyle)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items.toList())

        listPopupWindow.apply {
            anchorView = binding.toolbarMain.txtBaseCurrency
            setAdapter(adapter)
            setOnItemClickListener { parent, _, position, _ ->
                viewModel.setDefaultCurrency(parent.getItemAtPosition(position).toString())
                dismiss()
            }
        }
    }

    private fun showMenu(view: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(this, view)
        with(popup) {
            menuInflater.inflate(menuRes, menu)

            setOnMenuItemClickListener { item ->
                item?.itemId?.let { viewModel.setPopUpState(it) }
                false
            }

            show()
        }
    }
}