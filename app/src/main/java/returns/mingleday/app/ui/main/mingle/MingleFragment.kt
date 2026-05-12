package returns.mingleday.app.ui.main.mingle

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import returns.mingleday.R
import returns.mingleday.app.data.remote.model.mingle.MinglePermissionRequest
import returns.mingleday.app.data.remote.model.mingle.MingleType
import returns.mingleday.app.data.remote.network.onError
import returns.mingleday.app.data.remote.network.onException
import returns.mingleday.app.data.remote.network.onSuccess
import returns.mingleday.app.data.repository.CategoryRepository
import returns.mingleday.app.data.repository.MingleRepository
import returns.mingleday.app.ui.main.MainActivity
import returns.mingleday.databinding.FragmentMingleBinding
import returns.mingleday.util.DateFormatter.formatCustom
import java.time.LocalDateTime

class MingleFragment : Fragment() {

    private var _binding: FragmentMingleBinding? = null
    private val binding get() = _binding!!
    private val mingleRepository = MingleRepository()
    private val categoryRepository = CategoryRepository()
    private lateinit var mingleMemberAdapter: MingleMemberAdapter
    private lateinit var mingleCategoryAdapter: MingleCategoryAdapter

    private var isRealnameOn: Boolean = false
    private var isPermissionOn: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMingleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mingleId = arguments?.getInt("mingle_id") ?: return

        (requireActivity() as MainActivity).setToolbarTitle(R.string.mingle_name_title)

        setupRecyclerView(mingleId)
        setupMingleInviteButton(mingleId)
        setupLeaveButton(mingleId)
        setupFetchCategories(mingleId)
        setupFetchMingle(mingleId)
        setupToggles(mingleId)
    }

    private fun setupLeaveButton(mingleId: Int) {
        binding.leaveButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("밍글 나가기")
                .setMessage("정말 이 밍글에서 나가시겠습니까?")
                .setPositiveButton("확인") { _, _ ->

                    viewLifecycleOwner.lifecycleScope.launch {

                        mingleRepository.leaveMingle(mingleId)
                            .onSuccess {
                                Toast.makeText(
                                    requireContext(),
                                    "밍글에서 나갔습니다.",
                                    Toast.LENGTH_LONG
                                ).show()

                                requireActivity().supportFragmentManager.popBackStack()
                            }
                            .onError {
                                Toast.makeText(
                                    requireContext(),
                                    it,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            .onException {
                                Toast.makeText(
                                    requireContext(),
                                    "오류가 발생했습니다.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                }
                .setNegativeButton("취소", null)
                .show()
        }
    }

    private fun setToggleImage(imageView: ImageView, isOn: Boolean) {
        imageView.setImageResource(
            if (isOn) R.drawable.ic_toggle_on else R.drawable.ic_toggle_off
        )
    }

    private fun setupToggles(mingleId: Int) {
        setToggleImage(binding.toggleRealnameValue, false)
        setToggleImage(binding.togglePermissionValue, false)

        binding.toggleRealnameValue.setOnClickListener {
            val previousState = isRealnameOn

            isRealnameOn = !isRealnameOn
            setToggleImage(binding.toggleRealnameValue, isRealnameOn)

            viewLifecycleOwner.lifecycleScope.launch {
                mingleRepository.updateSetting(mingleId, "realname", isRealnameOn)
                    .onSuccess {
                        Log.d("MingleFragment", "변경 성공")
                        Toast.makeText(requireContext(), "밍글의 권한 사용 여부를 성공적으로 변경했습니다.", Toast.LENGTH_LONG).show()
                    }
                    .onError {
                        Log.d("MingleFragment", "변경 실패 - $it")
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        isRealnameOn = previousState
                        setToggleImage(binding.toggleRealnameValue, isRealnameOn)
                    }
                    .onException {
                        Log.d("MingleFragment", "변경 중 예외 발생 - $it")
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        isRealnameOn = previousState
                        setToggleImage(binding.toggleRealnameValue, isRealnameOn)
                    }
            }
        }

        binding.togglePermissionValue.setOnClickListener {
            val previousState = isPermissionOn

            isPermissionOn = !isPermissionOn
            setToggleImage(binding.togglePermissionValue, isPermissionOn)

            viewLifecycleOwner.lifecycleScope.launch {
                mingleRepository.updateSetting(mingleId, "permission", isPermissionOn)
                    .onSuccess {
                        Log.d("MingleFragment", "변경 성공")
                        Toast.makeText(requireContext(), "밍글의 실명 사용 여부를 성공적으로 변경했습니다.", Toast.LENGTH_LONG).show()
                    }
                    .onError {
                        Log.d("MingleFragment", "변경 실패 - $it")
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        isPermissionOn = previousState
                        setToggleImage(binding.togglePermissionValue, isPermissionOn)
                    }
                    .onException {
                        Log.d("MingleFragment", "변경 중 예외 발생 - $it")
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        isPermissionOn = previousState
                        setToggleImage(binding.togglePermissionValue, isPermissionOn)
                    }
            }
        }
    }

    private fun setupMingleInviteButton(mingleId: Int) {
        binding.inviteButton.setOnClickListener {
            EmailDialogFragment { email ->
                viewLifecycleOwner.lifecycleScope.launch {
                    mingleRepository.inviteMingle(mingleId, email)
                        .onSuccess {
                            Log.d("MingleFragment", "초대 요청 성공")
                            Toast.makeText(requireContext(), "해당 이메일로 성공적으로 요청을 보냈습니다.", Toast.LENGTH_LONG).show()
                        }
                        .onError {
                            Log.d("MingleFragment", "초대 요청 실패 - $it")
                        }
                        .onException {
                            Log.d("MingleFragment", "초대 중 예외 발생 - $it")
                        }
                }

            }.show(parentFragmentManager, "EmailDialog")
        }
    }

    private fun setupFetchMingle(mingleId: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            mingleRepository.getMingle(mingleId)
                .onSuccess { response ->
                    Log.d("MingleFragment", "밍글 정보 요청 성공")
                    setupGetTypeImage(response.mingleType)
                    mingleMemberAdapter.submitList(response.mingleMembers)

                    (requireActivity() as MainActivity).setToolbarTitle(response.mingleName)

                    val dt = LocalDateTime.parse(response.createdAt)
                    binding.registerDateValue.text = dt.formatCustom(1)
                    binding.mingleMemberCntValue.text = (response.mingleMembers.size+1).toString()
                    binding.ownerNameValue.text = response.ownerName
                    setToggleImage(binding.toggleRealnameValue, response.useRealname)
                    setToggleImage(binding.togglePermissionValue, response.usePermission)
                }
                .onError {
                    Log.d("MingleFragment", "밍글 요청 실패 - $it")
                    Toast.makeText(requireContext(), R.string.internal_server_error, Toast.LENGTH_LONG).show()
                }
                .onException {
                    Log.d("MingleFragment", "밍글 요청 도중 예외 발생 - $it")
                    Toast.makeText(requireContext(), R.string.internal_server_error, Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun setupRecyclerView(mingleId: Int) {
        // mingle category recycler view
        mingleCategoryAdapter = MingleCategoryAdapter(mingleId)
        binding.mingleCategoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        binding.mingleCategoryRecyclerView.adapter =
            mingleCategoryAdapter

        viewLifecycleOwner.lifecycleScope.launch {

            categoryRepository.getMingleCategory(mingleId)
                .onSuccess { response ->

                    mingleCategoryAdapter.submitList(response)
                }
        }

        // mingle member recycler view
        mingleMemberAdapter = MingleMemberAdapter(
            viewLifecycleOwner
        ) { memberId, permissionType, isAllowed ->

            var success = false

            mingleRepository.updateMemberPermission(
                mingleId = mingleId,
                mingleMemberId = memberId,
                minglePermissionRequest = MinglePermissionRequest(
                    permission = permissionType,
                    value = isAllowed
                )
            )
                .onSuccess {
                    Log.d("MingleFragment", "권한 변경 성공")
                    Toast.makeText(requireContext(), "해당 밍글 멤버의 권한을 성공적으로 변경했습니다.", Toast.LENGTH_LONG).show()
                    success = true
                }
                .onError {
                    Log.d("MingleFragment", "권한 변경 실패 - $it")
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    success = false
                }
                .onException {
                    Log.d("MingleFragment", "권한 변경 중 예외 발생 - $it")
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    success = false
                }

            success
        }

        binding.mingleMemberRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        binding.mingleMemberRecyclerView.adapter = mingleMemberAdapter
    }

    private fun setupGetTypeImage(mingleType: MingleType) {
        when(mingleType) {
            MingleType.FAMILY -> binding.mingleImageValue.setImageResource(R.drawable.bg_family_default)
            MingleType.FRIEND -> binding.mingleImageValue.setImageResource(R.drawable.bg_friend_default)
            MingleType.LOVER -> binding.mingleImageValue.setImageResource(R.drawable.bg_lover_default)
            MingleType.SCHOOL -> binding.mingleImageValue.setImageResource(R.drawable.bg_school_default)
            MingleType.COMPANY -> binding.mingleImageValue.setImageResource(R.drawable.bg_compamy_default)
            MingleType.CLUB -> binding.mingleImageValue.setImageResource(R.drawable.bg_club_default)
            MingleType.STUDY -> binding.mingleImageValue.setImageResource(R.drawable.bg_study_default)
            MingleType.CUSTOM -> binding.mingleImageValue.setImageResource(R.drawable.bg_custom_default)
            else -> binding.mingleImageValue.setImageResource(R.drawable.bg_custom_default)
        }
    }

    private fun setupFetchCategories(mingleId: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            categoryRepository.getMingleCategory(mingleId)
                .onSuccess { response ->
                    mingleCategoryAdapter.submitList(response)
                }
                .onError {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
                .onException {
                    Toast.makeText(requireContext(), "카테고리 조회 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
                }
        }
    }


}