package returns.mingleday.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

// 디스크 저장
class SettingsDataStore(
    private val context: Context
) {
    companion object {
        private val LANGUAGE = stringPreferencesKey("language")
    }

    suspend fun saveLanguage(lang: String) {
        context.dataStore.edit { prefs ->
            prefs[LANGUAGE] = lang
        }
    }

    suspend fun getLanguage(): String? {
        return context.dataStore.data.first()[LANGUAGE]
    }
}