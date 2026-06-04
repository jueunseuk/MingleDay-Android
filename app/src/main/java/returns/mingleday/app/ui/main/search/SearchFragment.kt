package returns.mingleday.app.ui.main.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import returns.mingleday.R
import returns.mingleday.app.data.remote.network.onError
import returns.mingleday.app.data.remote.network.onException
import returns.mingleday.app.data.remote.network.onSuccess
import returns.mingleday.app.data.repository.ScheduleRepository
import returns.mingleday.app.ui.main.MainActivity
import returns.mingleday.app.ui.main.schedule.ScheduleFragment
import returns.mingleday.app.util.ToastUtil
import returns.mingleday.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var searchJob: Job? = null
    private lateinit var searchScheduleAdapter: SearchScheduleAdapter
    private val scheduleRepository = ScheduleRepository()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).setToolbarTitle(R.string.search_title)

        setupValidation()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        searchScheduleAdapter = SearchScheduleAdapter { schedule ->
            val fragment = ScheduleFragment().apply {
                arguments = Bundle().apply {
                    putInt("mingleId", schedule.mingleId)
                    putLong("scheduleId", schedule.scheduleId)
                    putLong("scheduleInstanceId", schedule.scheduleInstanceId)
                    putString("scheduleName", schedule.title)
                }
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frame, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.searchScheduleRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.searchScheduleRecycler.adapter = searchScheduleAdapter
    }

    private fun setupSearchRequest(keyword: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            scheduleRepository.searchSchedule(keyword)
                .onSuccess { response ->
                    Log.d("SearchFragment", "검색 요청 중 발생 - 키워드 : $keyword")
                    if(response.isNotEmpty()) {
                        searchScheduleAdapter.submitList(response)
                        binding.searchScheduleRecycler.visibility = View.VISIBLE
                        binding.emptyResult.visibility = View.GONE
                    } else {
                        binding.searchScheduleRecycler.visibility = View.GONE
                        binding.emptyResult.visibility = View.VISIBLE
                    }
                }
                .onError {
                    Log.d("SearchFragment", "검색 요청 중 에러 발생 - $it")
                    ToastUtil.makeErrorToast(requireContext())
                }
                .onException {
                    Log.d("SearchFragment", "검색 요청 중 예외 발생 - $it")
                    ToastUtil.makeExceptionToast(requireContext(), it)
                }
        }
    }

    private fun setupValidation() {
        binding.keywordValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()

                searchJob = lifecycleScope.launch {
                    delay(1000)

                    val keyword = binding.keywordValue.text.toString()

                    if (keyword.length in 1..30) {
                        setupSearchRequest(keyword)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}