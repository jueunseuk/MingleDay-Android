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
import returns.mingleday.R
import returns.mingleday.databinding.FragmentInputNameBinding

class InputNameFragment : Fragment() {

    private var _binding: FragmentInputNameBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInputNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFocus()
        setupFocusChange()
        setupValidation()
        setupNextButton()
    }

    private fun setupFocus() {
        binding.inputNameValue.requestFocus()
    }

    private fun setupFocusChange() {
        binding.inputNameValue.setOnFocusChangeListener { _, hasFocus ->
            binding.inputNameLabel.isActivated = hasFocus
        }
    }

    private fun setupValidation() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isNameLengthValid = binding.inputNameValue.length() in 2..5
                binding.goNextButton.isEnabled = isNameLengthValid
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.inputNameValue.addTextChangedListener(watcher)
    }

    private fun setupNextButton() {
        binding.goNextButton.setOnClickListener {
            viewModel.name = binding.inputNameValue.text.toString().trim()

            Log.d("InputNameFragment", "이름 입력 완료")
            parentFragmentManager.beginTransaction()
                .replace(R.id.login_frame, InputPasswordFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}