package returns.mingleday.app.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import returns.mingleday.R
import returns.mingleday.app.data.remote.model.auth.Purpose
import returns.mingleday.app.data.remote.network.onError
import returns.mingleday.app.data.remote.network.onException
import returns.mingleday.app.data.remote.network.onSuccess
import returns.mingleday.databinding.FragmentVerifyBinding
import returns.mingleday.app.data.repository.AuthRepository

class VerifyFragment : Fragment() {

    private var _binding: FragmentVerifyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()
    private val authRepository = AuthRepository()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVerifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFocus()
        setupViewModel()
        setupFocusChange()
        setupValidation()
        setupResendButton()
        setupCheckButton()
    }

    private fun setupViewModel() {
        binding.inputEmailValue.setText(viewModel.email)
    }

    private fun setupFocus() {
        binding.inputVerificationCodeValue.requestFocus()
    }

    private fun setupFocusChange() {
        binding.inputVerificationCodeValue.setOnFocusChangeListener { _, hasFocus ->
            binding.inputVerificationCodeLabel.isActivated = hasFocus
        }
    }

    private fun setupValidation() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = binding.inputEmailValue.text.toString()
                val verificationCode = binding.inputVerificationCodeValue.text.toString()

                val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
                val isVerificationCodeLengthValid = verificationCode.length == 6

                binding.checkVerificationCodeButton.isEnabled = isEmailValid && isVerificationCodeLengthValid
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.inputEmailValue.addTextChangedListener(watcher)
        binding.inputVerificationCodeValue.addTextChangedListener(watcher)
    }

    private fun setupResendButton() {
        binding.resendButton.setOnClickListener {
            binding.checkVerificationCodeButton.isEnabled = true

            viewLifecycleOwner.lifecycleScope.launch {
                val purpose: Purpose = viewModel.purpose
                authRepository.sendVerificationCode(viewModel.email.toString(), purpose)
                    .onSuccess {
                        Log.d("SendFragment", "인증번호 재전송 성공")
                        viewModel.email = binding.inputEmailValue.text.toString()
                    }
                    .onError {
                        Log.d("SendFragment", "인증번호 전송 실패: $it")
                        binding.resendButton.isEnabled = true
                    }
                    .onException {
                        Log.e("SendFragment", "인증번호 전송 예외:, $it")
                        binding.resendButton.isEnabled = true
                    }
            }
        }
    }

    private fun setupCheckButton() {
        binding.checkVerificationCodeButton.setOnClickListener {
            binding.checkVerificationCodeButton.isEnabled = false
            binding.checkVerificationCodeButton.setText(R.string.requesting)

            viewLifecycleOwner.lifecycleScope.launch {
                authRepository.verifyCode(viewModel.email.toString(), binding.inputVerificationCodeValue.text.toString(), viewModel.purpose)
                    .onSuccess {
                        Log.d("VerifyFragment", "인증번호 확인 성공")
                        viewModel.email = viewModel.email.toString()

                        val nextFragment = if (viewModel.purpose == Purpose.REGISTER) {
                            InputNameFragment()
                        } else {
                            InputPasswordFragment()
                        }

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.login_frame, nextFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                    .onError {
                        Log.d("SendFragment", "인증번호 확인 실패: $it")
                        binding.checkVerificationCodeButton.isEnabled = true
                        binding.checkVerificationCodeButton.setText(R.string.verify_code_button)
                        Toast.makeText(requireContext(), R.string.internal_server_error, Toast.LENGTH_LONG).show()
                    }
                    .onException {
                        Log.e("SendFragment", "인증번호 확인 중 예외 발생:, $it")
                        binding.checkVerificationCodeButton.isEnabled = true
                        binding.checkVerificationCodeButton.setText(R.string.verify_code_button)
                        Toast.makeText(requireContext(), R.string.verification_mismatch, Toast.LENGTH_LONG).show()
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
