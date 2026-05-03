package returns.mingleday.app

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import returns.mingleday.app.data.local.TokenDataStore
import returns.mingleday.app.data.local.TokenProvider
import returns.mingleday.app.data.remote.network.RetrofitClient

class MingleDayApplication : Application() {

    lateinit var tokenDataStore: TokenDataStore
        private set

    override fun onCreate() {
        super.onCreate()

        RetrofitClient.init()
        tokenDataStore = TokenDataStore(this)

        CoroutineScope(Dispatchers.IO).launch {
            val token = tokenDataStore.getAccessToken()
            if (!token.isNullOrBlank()) {
                TokenProvider.setAccessToken(token)
            }
        }
    }
}