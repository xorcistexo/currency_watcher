package su.lalayan.currencywatcher.data.model.currency

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RatesResponse(
    @SerializedName("base")
    var base: String = "", // EUR
    @SerializedName("date")
    var date: String = "", // 2021-03-17
    @SerializedName("success")
    var success: Boolean = false, // true
    @SerializedName("timestamp")
    var timestamp: Long = 0L, // 1519296206
    @SerializedName("rates")
    @Expose
    var rates: Map<String, Double> = emptyMap()
)
