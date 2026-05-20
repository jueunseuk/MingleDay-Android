package returns.mingleday.app.ui.main.mingle

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import returns.mingleday.R
import returns.mingleday.app.data.remote.model.mingle.CreateMingleRequest
import returns.mingleday.app.data.remote.model.mingle.MingleType
import returns.mingleday.app.data.remote.network.onError
import returns.mingleday.app.data.remote.network.onException
import returns.mingleday.app.data.remote.network.onSuccess
import returns.mingleday.app.data.repository.MingleRepository
import returns.mingleday.app.ui.main.MainActivity
import returns.mingleday.databinding.FragmentMingleAddBinding

class MingleAddFragment : Fragment() {

    private var _binding: FragmentMingleAddBinding? = null
    private val binding get() = _binding!!
    private val mingleRepository = MingleRepository()

    private var selectedType: MingleType = MingleType.FAMILY
    private var isRealnameOn: Boolean = false
    private var isPermissionOn: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMingleAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).setToolbarTitle(R.string.add_mingle_title)
        setupAddMingle()
        setupValidation()
        setupMingleTypeSelection()
        setupBackButton()
        setupToggles()
    }

    private fun setupToggles() {
        binding.toggleRealnameValue.setOnClickListener {
            isRealnameOn = !isRealnameOn
            setToggleImage(binding.toggleRealnameValue, isRealnameOn)
        }

        binding.togglePermissionValue.setOnClickListener {
            isPermissionOn = !isPermissionOn
            setToggleImage(binding.togglePermissionValue, isPermissionOn)
        }
    }

    private fun setToggleImage(imageView: ImageView, isOn: Boolean) {
        imageView.setImageResource(
            if (isOn) R.drawable.ic_toggle_on else R.drawable.ic_toggle_off
        )
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupMingleTypeSelection() {
        val buttonMap = mapOf(
            binding.familyButton to MingleType.FAMILY,
            binding.friendButton to MingleType.FRIEND,
            binding.coupleButton to MingleType.LOVER,
            binding.schoolButton to MingleType.SCHOOL,
            binding.companyButton to MingleType.COMPANY,
            binding.clubButton to MingleType.CLUB,
            binding.studyButton to MingleType.STUDY,
            binding.customButton to MingleType.CUSTOM
        )

        binding.familyButton.isSelected = true
        selectedType = MingleType.FAMILY

        val buttons = buttonMap.keys.toList()

        buttons.forEach { button ->
            button.setOnClickListener {
                buttons.forEach { it.isSelected = false }

                button.isSelected = true
                selectedType = buttonMap[button]!!
            }
        }
    }

    private fun setupValidation() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isNameLengthValid = binding.inputMingleNameValue.length() in 1..30
                val isDescriptionValid = binding.inputMingleNameValue.length() > 0

                binding.addMingleButton.isEnabled = isNameLengthValid && isDescriptionValid
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.inputMingleNameValue.addTextChangedListener(watcher)
        binding.inputMingleDescriptionValue.addTextChangedListener(watcher)
    }

    private fun setupAddMingle() {
        binding.addMingleButton.setOnClickListener {
            binding.addMingleButton.isEnabled = false
            binding.addMingleButton.text = R.string.requesting.toString()

            viewLifecycleOwner.lifecycleScope.launch {
                mingleRepository.createMingle(CreateMingleRequest(
                    binding.inputMingleNameValue.text.toString(),
                    binding.inputMingleDescriptionValue.text.toString(),
                    isPermissionOn,
                    isRealnameOn,
                    selectedType
                ))
                    .onSuccess { response ->
                        Log.d("MingleAddFragment", "밍글 생성 요청 성공")
                        Toast.makeText(requireContext(), "밍글이 생성되었습니다.", Toast.LENGTH_LONG).show()
                        // 완료 후 밍글 정보 화면으로 이동 - 우선은 백

                        val fragment = MingleFragment().apply {
                            arguments = Bundle().apply {
                                putInt("mingle_id", response.mingleId)
                            }
                        }

                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.main_frame, fragment)
                            .addToBackStack(null)
                            .commit()
                    }
                    .onError {
                        Log.d("MingleAddFragment", "밍글 생성 요청 실패 - $it")

                        binding.addMingleButton.isEnabled = true
                        binding.addMingleButton.text = getString(R.string.add_mingle_button)
                    }
                    .onException {
                        Log.d("MingleAddFragment", "밍글 생성 요청 도중 예외 발생 - $it")
                        binding.addMingleButton.isEnabled = true
                        binding.addMingleButton.text = R.string.add_mingle_button.toString()
                    }
            }
        }
    }

    // 밍글이 하나도 없을 때 생성 유도 컴포넌트 만들기
}