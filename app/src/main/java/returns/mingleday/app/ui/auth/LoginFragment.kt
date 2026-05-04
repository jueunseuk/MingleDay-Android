package returns.mingleday.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import returns.mingleday.R
import returns.mingleday.app.MainActivity
import returns.mingleday.app.MingleDayApplication
import returns.mingleday.app.data.local.TokenProvider
import returns.mingleday.app.data.remote.network.onError
import returns.mingleday.app.data.remote.network.onException
import returns.mingleday.app.data.remote.network.onSuccess
import returns.mingleday.databinding.FragmentLoginBinding
import returns.mingleday.domain.repository.AuthRepository

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val authRepository = AuthRepository()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpFocus()
        setupFocusChange()
        setupValidation()
        setupLoginButton()

        binding.goToSignupButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.login_frame, SendFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.goToResetPasswordButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.login_frame, ResetFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setUpFocus() {
        binding.inputEmailValue.requestFocus()
    }

    private fun setupFocusChange() {
        binding.inputEmailValue.setOnFocusChangeListener { _, hasFocus ->
            binding.inputEmailLabel.isActivated = hasFocus
        }

        binding.inputPasswordValue.setOnFocusChangeListener { _, hasFocus ->
            binding.inputPasswordLabel.isActivated = hasFocus
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
        val isPasswordLengthValid = password.length >= 8

        binding.loginButton.isEnabled = isEmailValid && isPasswordLengthValid
    }

    private fun setupLoginButton() {
        binding.loginButton.setOnClickListener {
            binding.loginButton.setText(R.string.requesting)
            binding.loginButton.isEnabled = false

            viewLifecycleOwner.lifecycleScope.launch {
                authRepository.login(
                    binding.inputEmailValue.text.toString(),
                    binding.inputPasswordValue.text.toString()
                )
                    .onSuccess { tokenResponse ->
                        Log.d("LoginFragment", "로그인 성공")
                        val token = tokenResponse.token
                        val app = requireActivity().application as MingleDayApplication
                        app.tokenDataStore.saveAccessToken(token)
                        TokenProvider.setAccessToken(token)

                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    .onError {
                        Log.d("LoginFragment", "로그인 실패: $it")
                        binding.loginButton.isEnabled = true
                        binding.loginButton.setText(R.string.login_button)
                    }
                    .onException {
                        Log.e("LoginFragment", "로그인 도중 예외 발생:, $it")
                        binding.loginButton.isEnabled = true
                        binding.loginButton.setText(R.string.login_button)
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}