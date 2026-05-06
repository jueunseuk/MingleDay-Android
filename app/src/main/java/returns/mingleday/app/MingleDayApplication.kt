package returns.mingleday.app

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import returns.mingleday.app.data.local.SettingsDataStore
import returns.mingleday.app.data.local.TokenDataStore
import returns.mingleday.app.data.local.TokenProvider
import returns.mingleday.app.data.remote.network.RetrofitClient

class MingleDayApplication : Application() {

    lateinit var tokenDataStore: TokenDataStore
        private set

    lateinit var settingsDataStore: SettingsDataStore
        private set

    override fun onCreate() {
        super.onCreate()

        tokenDataStore = TokenDataStore(this)
        settingsDataStore = SettingsDataStore(this)

        RetrofitClient.init(this)

        CoroutineScope(Dispatchers.IO).launch {
            val token = tokenDataStore.getAccessToken()
            if (!token.isNullOrBlank()) {
                TokenProvider.setAccessToken(token)
            }
        }
    }
}