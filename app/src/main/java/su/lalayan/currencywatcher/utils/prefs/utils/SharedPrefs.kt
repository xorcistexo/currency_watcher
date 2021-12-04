package su.lalayan.currencywatcher.utils.prefs.utils

import android.content.Context
import android.content.SharedPreferences
import su.lalayan.currencywatcher.BuildConfig

@Suppress("UNCHECKED_CAST")
class SharedPrefs(context: Context) {
    companion object {
        private const val PREF = BuildConfig.APPLICATION_ID
        private const val PREF_DEFAULT_CURRENCY = "default_currency"

        private const val DEFAULT_CURRENCY_DEF_VALUE = "EUR"
    }

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)

    fun saveDefaultCurrency(token: String) = put(PREF_DEFAULT_CURRENCY, token)

    fun getDefaultCurrency() = get(PREF_DEFAULT_CURRENCY, String::class.java)

    private fun <T> get(key: String, clazz: Class<T>): T {
        return when (clazz) {
            String::class.java -> when (key) {
                PREF_DEFAULT_CURRENCY -> sharedPref.getString(key, DEFAULT_CURRENCY_DEF_VALUE)
                else -> sharedPref.getString(key, "")
            }
            Boolean::class.java -> sharedPref.getBoolean(key, false)
            Float::class.java -> sharedPref.getFloat(key, -1f)
            Double::class.java -> sharedPref.getFloat(key, -1f)
            Int::class.java -> sharedPref.getInt(key, -1)
            Long::class.java -> sharedPref.getLong(key, -1L)
            else -> null
        } as T
    }

    private fun <T> put(key: String, data: T) {
        val editor = sharedPref.edit()
        when (data) {
            is String -> editor.putString(key, data)
            is Boolean -> editor.putBoolean(key, data)
            is Float -> editor.putFloat(key, data)
            is Double -> editor.putFloat(key, data.toFloat())
            is Int -> editor.putInt(key, data)
            is Long -> editor.putLong(key, data)
        }
        editor.apply()
    }

    fun clear() {
        sharedPref.edit().run {
            remove(PREF_DEFAULT_CURRENCY)
        }.apply()
    }
}