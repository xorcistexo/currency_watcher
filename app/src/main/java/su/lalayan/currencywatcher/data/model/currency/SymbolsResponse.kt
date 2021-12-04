package su.lalayan.currencywatcher.data.model.currency


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SymbolsResponse(
    @SerializedName("success")
    var success: Boolean = false, // true
    @SerializedName("symbols")
    @Expose
    var symbols: Map<String, String> = mutableMapOf()
)