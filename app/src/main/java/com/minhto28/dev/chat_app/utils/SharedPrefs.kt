import android.content.Context
import android.content.SharedPreferences
import com.minhto28.dev.chat_app.application.App
import com.minhto28.dev.chat_app.models.Account

class SharedPrefs private constructor() {
    private val mSharedPreferences: SharedPreferences

    init {
        mSharedPreferences = App.self()!!.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    operator fun get(key: String): Account? {
        val jsonFromPrefs = mSharedPreferences.getString(key, null)
        if (jsonFromPrefs != null) {
            return App.self()?.gSon?.fromJson(jsonFromPrefs, Account::class.java)
        }
        return null
    }


    fun <T> put(data: T) {
        val editor = mSharedPreferences.edit()
        editor.putString(ACCOUNT, App.self()?.gSon?.toJson(data))
        editor.apply()
    }

    fun clear() {
        mSharedPreferences.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "share_prefs"
        private var mInstance: SharedPrefs? = null
        val ACCOUNT = "ACCOUNT"
        val instance: SharedPrefs
            get() {
                if (mInstance == null) {
                    mInstance = SharedPrefs()
                }
                return mInstance as SharedPrefs
            }
    }
}