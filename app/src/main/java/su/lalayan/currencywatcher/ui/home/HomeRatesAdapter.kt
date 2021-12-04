package su.lalayan.currencywatcher.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import su.lalayan.currencywatcher.databinding.ItemCurrencyBinding
import su.lalayan.currencywatcher.data.model.currency.RatesResponse

class HomeRatesAdapter(private val ratesList: MutableMap<String, Double>) :
    RecyclerView.Adapter<HomeRatesAdapter.ViewHolder>() {

    interface OnItemTap {
        fun onTap(product: RatesResponse)
    }

    fun setItemTapListener(onItemTap: OnItemTap) {
        onTapListener = onItemTap
    }

    fun getList() = ratesList

    private var onTapListener: OnItemTap? = null

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(rates: Map<String, Double>) {
        ratesList.clear()
        ratesList.putAll(rates)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val itemBinding: ItemCurrencyBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(rates: Pair<String, Double?>) {
            itemBinding.currencyNameTextView.text = rates.first // currency
            itemBinding.currencyValueTextView.text = rates.second.toString() // rate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemCurrencyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(
            Pair(
                ratesList.keys.toTypedArray()[position], // map key >> currency title
                ratesList[ratesList.keys.toTypedArray()[position]] // map value >> currency rate
            )
        )

    override fun getItemCount() = ratesList.size
}