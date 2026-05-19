package returns.mingleday.app.ui.main.mingle

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.fragment.app.DialogFragment
import returns.mingleday.app.data.remote.model.category.CategoryResponse
import returns.mingleday.databinding.FragmentCategoryEditDialogBinding
import returns.mingleday.app.util.ColorUtil

class CategoryEditDialogFragment(
    private val category: CategoryResponse,
    private val onConfirm: (
        name: String,
        description: String,
        textColor: String,
        backgroundColor: String
    ) -> Unit
) : DialogFragment() {

    private var _binding: FragmentCategoryEditDialogBinding? = null
    private val binding get() = _binding!!
    private var validation = false
    private lateinit var positiveButton: Button

    override fun onStart() {
        super.onStart()

        positiveButton = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.isEnabled = validation
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentCategoryEditDialogBinding.inflate(layoutInflater)

        setupDialog(category)
        setupValidation()

        return AlertDialog.Builder(requireContext())
            .setTitle(category.name+" 수정")
            .setView(binding.root)
            .setPositiveButton("수정") { _, _ ->
                onConfirm(
                    binding.inputNameValue.text.toString(),
                    binding.inputDescriptionValue.text.toString(),
                    binding.inputTextColorValue.text.toString(),
                    binding.inputBackgroundColorValue.text.toString()
                )
            }
            .setNegativeButton("취소", null)
            .create()
    }

    private fun setupDialog(item: CategoryResponse) {
        binding.inputNameValue.setText(item.name)
        binding.inputDescriptionValue.setText(item.description)
        binding.inputTextColorValue.setText(item.textColor)
        binding.inputBackgroundColorValue.setText(item.backgroundColor)
    }

    private fun setupValidation() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isNameLengthValid = binding.inputNameValue.length() > 0
                val isTextColorValid = binding.inputTextColorValue.length() == 6
                val isBGColorValid = binding.inputBackgroundColorValue.length() == 6
                val isTextHex = ColorUtil.isHexColor(binding.inputTextColorValue.text.toString())
                val isBGHex = ColorUtil.isHexColor(binding.inputBackgroundColorValue.text.toString())
                validation = isNameLengthValid && isTextColorValid && isBGColorValid && isTextHex && isBGHex

                if (::positiveButton.isInitialized) {
                    positiveButton.isEnabled = validation
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.inputNameValue.addTextChangedListener(watcher)
        binding.inputBackgroundColorValue.addTextChangedListener(watcher)
        binding.inputTextColorValue.addTextChangedListener(watcher)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}