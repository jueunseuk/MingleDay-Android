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
import returns.mingleday.app.data.remote.network.onError
import returns.mingleday.app.data.remote.network.onException
import returns.mingleday.app.data.remote.network.onSuccess
import returns.mingleday.app.data.repository.MingleRepository
import returns.mingleday.app.ui.main.MainActivity
import returns.mingleday.databinding.FragmentMingleListBinding

class MingleListFragment : Fragment() {

    private var _binding: FragmentMingleListBinding? = null
    private val binding get() = _binding!!
    private val mingleRepository = MingleRepository()
    private lateinit var mingleAdapter: MingleAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMingleListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).setToolbarTitle(R.string.mingle_list_title)
        setupRecyclerView()
        setupFetchMingleList()
        setupMoveAddMingle()
    }

    private fun setupMoveAddMingle() {
        binding.addMingleButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frame, MingleAddFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupRecyclerView() {
        mingleAdapter = MingleAdapter { mingle ->
            Log.d("MingleListFragment", "클릭: ${mingle.mingleName}")

            val fragment = MingleFragment().apply {
                arguments = Bundle().apply {
                    putInt("mingle_id", mingle.mingleId)
                }
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frame, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.mingleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.mingleRecyclerView.adapter = mingleAdapter
    }

    private fun setupFetchMingleList() {
        viewLifecycleOwner.lifecycleScope.launch {
            mingleRepository.getMyMingles()
                .onSuccess { response ->
                    Log.d("MingleListFragment", "밍글 목록 요청 성공")
                    if(response.isEmpty()) {
                        binding.mingleListCntValue.text = "아래의 버튼을 눌러 새로운 밍글을 만들어보세요!"
                    } else binding.mingleListCntValue.text = "소속된 밍글 ${response.size}개"
                    mingleAdapter.submitList(response)
                }
                .onError {
                    Log.d("MingleListFragment", "밍글 목록 요청 실패 - $it")
                    Toast.makeText(requireContext(), R.string.internal_server_error, Toast.LENGTH_LONG).show()
                }
                .onException {
                    Log.d("MingleListFragment", "밍글 목록 요청 도중 예외 발생 - $it")
                    Toast.makeText(requireContext(), R.string.internal_server_error, Toast.LENGTH_LONG).show()
                }
        }
    }
}