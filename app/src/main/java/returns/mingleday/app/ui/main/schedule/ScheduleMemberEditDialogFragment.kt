package returns.mingleday.app.ui.main.schedule

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import returns.mingleday.app.data.remote.model.schedule.ScheduleMemberResponse
import returns.mingleday.databinding.FragmentScheduleMemberEditDialogBinding

class ScheduleMemberEditDialogFragment(
    private val member: ScheduleMemberResponse,
    private val onConfirm: (
        memo: String
    ) -> Unit
) : DialogFragment() {

    private var _binding: FragmentScheduleMemberEditDialogBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentScheduleMemberEditDialogBinding.inflate(layoutInflater)

        binding.inputMemoValue.setText(member.memo)

        return AlertDialog.Builder(requireContext())
            .setTitle(member.name+"의 메모 수정")
            .setView(binding.root)
            .setPositiveButton("수정") { _, _ ->
                onConfirm(
                    binding.inputMemoValue.text.toString()
                )
            }
            .setNegativeButton("취소", null)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}