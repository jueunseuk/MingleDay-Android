package returns.mingleday.app.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import returns.mingleday.R
import returns.mingleday.app.data.remote.model.auth.Purpose
import returns.mingleday.app.data.remote.network.onError
import returns.mingleday.app.data.remote.network.onException
import returns.mingleday.app.data.remote.network.onSuccess
import returns.mingleday.databinding.FragmentInputPasswordBinding
import returns.mingleday.app.data.repository.AuthRepository
import returns.mingleday.app.util.ToastUtil

class InputPasswordFragment : Fragment() {

    private var _binding: FragmentInputPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()
    private val authRepository = AuthRepository()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInputPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFocus()
        setupFocusChange()
        setupValidation()
        when (viewModel.purpose) {
            Purpose.REGISTER -> setupNextButton()
            Purpose.REISSUE -> setupResetPasswordCompleteButton()
            else -> {}
        }
    }

    private fun setupFocus() {
        binding.inputPasswordValue.requestFocus()
    }

    private fun setupFocusChange() {
        binding.inputPasswordValue.setOnFocusChangeListener { _, hasFocus ->
            binding.inputPasswordLabel.isActivated = hasFocus
        }
    }

    private fun setupValidation() {
        validatePasswordInput()

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePasswordInput()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.inputPasswordValue.addTextChangedListener(watcher)
        binding.inputPasswordCheckValue.addTextChangedListener(watcher)
    }

    private fun validatePasswordInput() {
        val password = binding.inputPasswordValue.text.toString().trim()
        val passwordCheck = binding.inputPasswordCheckValue.text.toString().trim()

        val isLengthValid = password.length in 8..20

        val hasEnglish = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { it in "!@#$%^&*()_-+=[]{}.?".toSet() }
        val isCombinationValid = hasEnglish && hasDigit && hasSpecial

        val allowedRegex = Regex("^[A-Za-z0-9!@#$%^&*()_\\-+=\\[\\]{}.?]*$")
        val isSpecialCharValid = allowedRegex.matches(password)

        val isSamePasswordInput =
            password.isNotEmpty() && passwordCheck.isNotEmpty() && password == passwordCheck

        binding.passwordHint1.isActivated = isLengthValid
        binding.passwordHint2.isActivated = isCombinationValid
        binding.passwordHint3.isActivated = isSpecialCharValid
        binding.passwordSameHint.isActivated = isSamePasswordInput

        binding.goNextButton.isEnabled =
            isLengthValid && isCombinationValid &&
                    isSpecialCharValid && isSamePasswordInput
    }

    private fun setupNextButton() {
        binding.goNextButton.setOnClickListener {
            viewModel.password = binding.inputPasswordValue.text.toString().trim()

            Log.d("InputPasswordFragment", "비밀번호 입력 완료")
            parentFragmentManager.beginTransaction()
                .replace(R.id.login_frame, InputNicknameFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupResetPasswordCompleteButton() {
        binding.goNextButton.setText(R.string.reset_password_button)

        binding.goNextButton.setOnClickListener {
            viewModel.password = binding.inputPasswordValue.text.toString().trim()
            binding.goNextButton.isEnabled = false

            viewLifecycleOwner.lifecycleScope.launch {
                authRepository.resetPassword(
                    viewModel.email.toString(),
                    viewModel.password.toString(),
                )
                    .onSuccess {
                        Log.d("InputPasswordFragment", "비밀번호 변경 성공")
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.login_frame, LoginFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                    .onError {
                        Log.d("InputPasswordFragment", "비밀번호 변경 실패: $it")
                        ToastUtil.makeErrorToast(requireContext())
                        binding.goNextButton.isEnabled = true
                    }
                    .onException {
                        Log.e("InputPasswordFragment", "비밀번호 변경 도중 예외 발생:, $it")
                        ToastUtil.makeExceptionToast(requireContext(), it)
                        binding.goNextButton.isEnabled = true
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}