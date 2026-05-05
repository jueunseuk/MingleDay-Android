package returns.mingleday.app.ui.main.mingle

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import returns.mingleday.R
import returns.mingleday.databinding.FragmentEmailDialogBinding

class EmailDialogFragment(
    private val onSubmit: (String) -> Unit
) : DialogFragment() {

    private var _binding: FragmentEmailDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentEmailDialogBinding.inflate(layoutInflater)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}