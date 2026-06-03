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
import returns.mingleday.app.data.remote.network.onError
import returns.mingleday.app.data.remote.network.onException
import returns.mingleday.app.data.remote.network.onSuccess
import returns.mingleday.databinding.FragmentSendBinding
import returns.mingleday.app.data.repository.AuthRepository
import returns.mingleday.app.util.ToastUtil

class SendFragment : Fragment() {

    private var _binding: FragmentSendBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()
    private val authRepository = AuthRepository()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpFocus()
        setupFocusChange()
        setupValidation()
        setupSendButton()
    }

    private fun setUpFocus() {
        binding.inputEmailValue.requestFocus()
    }

    private fun setupFocusChange() {
        binding.inputEmailValue.setOnFocusChangeListener { _, hasFocus ->
            binding.inputEmailLabel.isActivated = hasFocus
        }
    }

    private fun setupValidation() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = binding.inputEmailValue.text.toString()

                val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()

                binding.sendVerificationCodeButton.isEnabled = isEmailValid
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.inputEmailValue.addTextChangedListener(watcher)
    }

    private fun setupSendButton() {
        binding.sendVerificationCodeButton.setOnClickListener {
            binding.sendVerificationCodeButton.setText(R.string.requesting)
            binding.sendVerificationCodeButton.isEnabled = false

            viewModel.email = binding.inputEmailValue.text.toString()

            viewLifecycleOwner.lifecycleScope.launch {

                authRepository.sendVerificationCode(viewModel.email.toString(), viewModel.purpose)
                    .onSuccess {
                        Log.d("SendFragment", "인증번호 전송 성공")
                        viewModel.email = binding.inputEmailValue.text.toString()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.login_frame, VerifyFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                    .onError {
                        Log.d("SendFragment", "인증번호 전송 실패: $it")
                        binding.sendVerificationCodeButton.isEnabled = true
                        binding.sendVerificationCodeButton.setText(R.string.send_button)
                        ToastUtil.makeErrorToast(requireContext())
                    }
                    .onException {
                        Log.e("SendFragment", "인증번호 전송 예외:, $it")
                        binding.sendVerificationCodeButton.isEnabled = true
                        binding.sendVerificationCodeButton.setText(R.string.send_button)
                        ToastUtil.makeExceptionToast(requireContext(), it)
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}