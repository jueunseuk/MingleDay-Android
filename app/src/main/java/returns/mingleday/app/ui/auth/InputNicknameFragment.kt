package returns.mingleday.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import returns.mingleday.R
import returns.mingleday.app.ui.main.MainActivity
import returns.mingleday.app.MingleDayApplication
import returns.mingleday.app.data.local.TokenProvider
import returns.mingleday.app.data.remote.network.onError
import returns.mingleday.app.data.remote.network.onException
import returns.mingleday.app.data.remote.network.onSuccess
import returns.mingleday.databinding.FragmentInputNicknameBinding
import returns.mingleday.app.data.repository.AuthRepository
import returns.mingleday.app.util.ToastUtil

class InputNicknameFragment : Fragment() {

    private var _binding: FragmentInputNicknameBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()
    private val authRepository = AuthRepository()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInputNicknameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFocus()
        setupFocusChange()
        setupValidation()
        setupSignupCompleteButton()
    }

    private fun setupFocus() {
        binding.inputNicknameValue.requestFocus()
    }

    private fun setupFocusChange() {
        binding.inputNicknameValue.setOnFocusChangeListener { _, hasFocus ->
            binding.inputNicknameLabel.isActivated = hasFocus
        }
    }

    private fun setupValidation() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isNameLengthValid = binding.inputNicknameValue.length() in 2..10
                binding.signupButton.isEnabled = isNameLengthValid
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.inputNicknameValue.addTextChangedListener(watcher)
    }

    private fun setupSignupCompleteButton() {
        binding.signupButton.setOnClickListener {
            viewModel.nickname = binding.inputNicknameValue.text.toString().trim()

            viewLifecycleOwner.lifecycleScope.launch {
                binding.signupButton.isEnabled = false

                authRepository.signup(
                    viewModel.email.toString(),
                    viewModel.name.toString(),
                    viewModel.password.toString(),
                    viewModel.nickname.toString()
                )
                    .onSuccess { tokenResponse ->
                        val token = tokenResponse.token
                        Log.d("SendFragment", "회원가입 성공")

                        val app = requireActivity().application as MingleDayApplication
                        app.tokenDataStore.saveAccessToken(token)
                        TokenProvider.setAccessToken(token)

                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    .onError {
                        Log.d("SendFragment", "회원가입 요청 실패: $it")
                        binding.signupButton.isEnabled = true
                        ToastUtil.makeErrorToast(requireContext())
                    }
                    .onException {
                        Log.e("SendFragment", "회원가입 도중 예외 발생:, $it")
                        binding.signupButton.isEnabled = true
                        Toast.makeText(requireContext(), R.string.invalid_input, Toast.LENGTH_LONG).show()
                        ToastUtil.makeExceptionToast(requireContext(), it)
                    }
            }
        }
    }
}