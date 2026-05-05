package returns.mingleday.app.ui.main.mingle

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import returns.mingleday.R
import returns.mingleday.app.data.remote.model.mingle.MingleType
import returns.mingleday.app.data.remote.network.onError
import returns.mingleday.app.data.remote.network.onException
import returns.mingleday.app.data.remote.network.onSuccess
import returns.mingleday.app.data.repository.MingleRepository
import returns.mingleday.app.ui.main.MainActivity
import returns.mingleday.databinding.FragmentMingleBinding

class MingleFragment : Fragment() {

    private var _binding: FragmentMingleBinding? = null
    private val binding get() = _binding!!
    private val mingleRepository = MingleRepository()
    private lateinit var mingleMemberAdapter: MingleMemberAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMingleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mingleId = arguments?.getInt("mingle_id") ?: return

        (requireActivity() as MainActivity).setToolbarTitle(R.string.mingle_name_title)
        setupRecyclerView()
        setupFetchMingle(mingleId)
        setupMingleInviteButton(mingleId)
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
                    (requireActivity() as MainActivity).setToolbarTitle(response.mingleName)
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

    private fun setupRecyclerView() {
        mingleMemberAdapter = MingleMemberAdapter { memberId, permissionType, isAllowed ->
            Log.d("MingleFragment", "$memberId / $permissionType / $isAllowed")
        }

        binding.mingleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.mingleRecyclerView.adapter = mingleMemberAdapter
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
}