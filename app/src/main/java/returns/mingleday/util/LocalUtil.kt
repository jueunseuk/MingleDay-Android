package returns.mingleday.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleUtil {
    fun applyLanguage(context: Context, language: String): Context {
        val locale = Locale.Builder()
            .setLanguage(language)
            .build()

        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}