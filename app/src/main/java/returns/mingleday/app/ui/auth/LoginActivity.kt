package returns.mingleday.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import returns.mingleday.app.MainActivity
import returns.mingleday.app.data.local.TokenDataStore
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

            if (accessToken != null) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.inputEmailValue.setOnFocusChangeListener { _, hasFocus ->
            binding.inputEmailLabel.isActivated = hasFocus
        }

        binding.inputPasswordValue.setOnFocusChangeListener { _, hasFocus ->
            binding.inputPasswordLabel.isActivated = hasFocus
        }

        setupValidation()

        binding.loginButton.setOnClickListener {
            // 로그인 추가
        }
    }

    private fun setupValidation() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateLoginForm()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.inputEmailValue.addTextChangedListener(watcher)
        binding.inputPasswordValue.addTextChangedListener(watcher)
    }

    private fun validateLoginForm() {
        val email = binding.inputEmailValue.text.toString()
        val password = binding.inputPasswordValue.text.toString()

        val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordLengthValid = password.length in 8..20

        binding.loginButton.isEnabled = isEmailValid && isPasswordLengthValid
    }
}