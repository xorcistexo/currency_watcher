package su.lalayan.currencywatcher.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import su.lalayan.currencywatcher.R
import su.lalayan.currencywatcher.databinding.FragmentHomeBinding
import su.lalayan.currencywatcher.data.model.currency.RatesResponse
import su.lalayan.currencywatcher.ui.FilterPopUpState
import su.lalayan.currencywatcher.ui.MainViewModel
import su.lalayan.currencywatcher.utils.ext.gone
import su.lalayan.currencywatcher.utils.ext.showToast
import su.lalayan.currencywatcher.utils.ext.visible

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val homeViewModel by viewModels<HomeViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeRatesAdapter: HomeRatesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        initUI()
    }

    private fun initUI() {
        setupRecyclerView()
        observeUI()
    }

    private fun setupRecyclerView() {
        homeRatesAdapter = HomeRatesAdapter(mutableMapOf())
        homeRatesAdapter.setItemTapListener(object : HomeRatesAdapter.OnItemTap {
            override fun onTap(product: RatesResponse) {
                // TODO implement favorite currency functionality
            }
        })

        binding.ratesRecyclerView.apply {
            adapter = homeRatesAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }
    }

    private fun observeUI() {
        observeDefaultCurrency()
        observeState()
        observeRates()
        observePopUpState()
    }

    private fun observeDefaultCurrency() {
        mainViewModel.defaultCurrency
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                handleDefaultCurrency()
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeState() {
        homeViewModel.mState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                handleState(state)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeRates() {
        homeViewModel.ratesResponse
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { ratesResponse ->
                ratesResponse?.let { response ->
                    handleRates(response)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observePopUpState() {
        mainViewModel.popUpState
            .flowWithLifecycle(lifecycle)
            .onEach { popUpState ->
                handlePopUpState(popUpState)
            }.launchIn(lifecycleScope)
    }

    private fun handlePopUpState(popUpState: FilterPopUpState) = when (popUpState) {
        is FilterPopUpState.AtoZ -> homeRatesAdapter.updateList(
            homeRatesAdapter.getList().toSortedMap()
        )
        is FilterPopUpState.ZtoA -> homeRatesAdapter.updateList(
            homeRatesAdapter.getList().toSortedMap(reverseOrder())
        )
        is FilterPopUpState.ByValueAscending -> homeRatesAdapter.updateList(
            homeRatesAdapter.getList().entries.sortedBy { it.value }
                .associate { it.toPair() }
        )
        is FilterPopUpState.ByValueDescending -> homeRatesAdapter.updateList(
            homeRatesAdapter.getList().entries.sortedBy { it.value }.reversed()
                .associate { it.toPair() }
        )
    }

    private fun handleState(state: HomeFragmentState) {
        when (state) {
            is HomeFragmentState.IsLoading -> handleLoading(state.isLoading)
            is HomeFragmentState.ShowToast -> requireActivity().showToast(state.message)
            is HomeFragmentState.Init -> Unit
        }
    }

    private fun handleLoading(isLoading: Boolean) = when {
        isLoading -> binding.loadingProgressBar.visible()
        else -> binding.loadingProgressBar.gone()
    }

    private fun handleRates(ratesResponse: RatesResponse) {
        binding.ratesRecyclerView.adapter?.let { adapter ->
            if (adapter is HomeRatesAdapter) adapter.updateList(ratesResponse.rates)
        }
    }

    private fun handleDefaultCurrency() = homeViewModel.fetchCurrencyList()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}