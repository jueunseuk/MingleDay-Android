package returns.mingleday.app.ui.main.mingle

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.fragment.app.DialogFragment
import returns.mingleday.R
import returns.mingleday.databinding.FragmentEmailDialogBinding

class EmailDialogFragment(
    private val onSubmit: (String) -> Unit
) : DialogFragment() {

    private var _binding: FragmentEmailDialogBinding? = null
    private val binding get() = _binding!!
    private var validation = false
    private lateinit var positiveButton: Button

    override fun onStart() {
        super.onStart()

        positiveButton = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.isEnabled = validation
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentEmailDialogBinding.inflate(layoutInflater)

        setupValidation()

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.email_label)
            .setView(binding.root)
            .setPositiveButton("확인") { _, _ ->
                val email = binding.emailInputValue.text.toString()
                onSubmit(email)
            }
            .setNegativeButton("취소", null)
            .create()
    }

    private fun setupValidation() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validation = android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailInputValue.text.toString()).matches()
                positiveButton.isEnabled = validation
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.emailInputValue.addTextChangedListener(watcher)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}