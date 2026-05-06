package returns.mingleday.app.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import returns.mingleday.app.ui.main.MainActivity
import returns.mingleday.app.data.local.TokenDataStore
import returns.mingleday.app.data.local.TokenProvider
import returns.mingleday.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tokenDataStore = TokenDataStore(this)

        lifecycleScope.launch {
            val accessToken = tokenDataStore.getAccessToken()

            if (!accessToken.isNullOrBlank()) {
                TokenProvider.setAccessToken(accessToken)

                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(binding.loginFrame.id, LoginFragment())
                    .commit()
            }
        }
    }
}
