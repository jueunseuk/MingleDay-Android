package returns.mingleday.app.ui.main.mingle

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import returns.mingleday.R
import returns.mingleday.databinding.FragmentCategoryDeleteDialogBinding

class CategoryDeleteDialogFragment(
    private val categoryName: String,
    private val onConfirm: () -> Unit
) : DialogFragment() {

    private var _binding: FragmentCategoryDeleteDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentCategoryDeleteDialogBinding.inflate(layoutInflater)

        return AlertDialog.Builder(requireContext())
            .setTitle(categoryName + getString(R.string.category_delete_alert))
            .setView(binding.root)
            .setPositiveButton("확인") { _, _ ->
                onConfirm()
            }
            .setNegativeButton("취소", null)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}