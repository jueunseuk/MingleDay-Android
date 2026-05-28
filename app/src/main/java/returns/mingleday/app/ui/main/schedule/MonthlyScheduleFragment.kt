package returns.mingleday.app.ui.main.schedule

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.launch
import returns.mingleday.R
import returns.mingleday.app.data.remote.model.schedule.AnniversaryItemWithType
import returns.mingleday.app.data.remote.model.schedule.MonthlyScheduleResponse
import returns.mingleday.app.data.remote.network.onError
import returns.mingleday.app.data.remote.network.onException
import returns.mingleday.app.data.remote.network.onSuccess
import returns.mingleday.app.data.repository.AnniversaryRepository
import returns.mingleday.app.data.repository.ScheduleRepository
import returns.mingleday.app.ui.main.MainActivity
import returns.mingleday.databinding.FragmentMonthlyScheduleBinding
import java.time.LocalDate

class MonthlyScheduleFragment : Fragment() {

    private var _binding: FragmentMonthlyScheduleBinding? = null
    private val binding get() = _binding!!
    private val scheduleRepository = ScheduleRepository()
    private val anniversaryRepository = AnniversaryRepository()
    private lateinit var calendarAdapter: CalendarAdapter

    private var year: Int = LocalDate.now().year
    private var month: Int = LocalDate.now().month.value

    private var mingleId: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMonthlyScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = requireActivity() as MainActivity
        mainActivity.setToolbarTitle(R.string.my_schedule_title)

        mingleId = arguments?.getInt("mingleId") ?: -1

        mainActivity.mingleId = mingleId

        setupDate(year, month)
        setupChangeDateButton()
        setupCalendarRecyclerView()
        if (mingleId == -1) {
            fetchMySchedules()
        } else {
            fetchSchedules(mingleId)
        }
        fetchAnniversarySchedules()
    }

    private fun setupCalendarRecyclerView() {
        calendarAdapter = CalendarAdapter { day ->
            // 날짜 클릭 시 일별 일정 조회 또는 상세 화면 이동 처리
            Log.d("MonthlyScheduleFragment", "${year}년 ${month}월 ${day}일 클릭")
        }

        binding.calendarRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 7)
            adapter = calendarAdapter
            isNestedScrollingEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }
    }

    private fun setupChangeDateButton() {
        binding.minusYearButton.setOnClickListener {
            setupDate(--year, month)
        }

        binding.minusMonthButton.setOnClickListener {
            setupDate(year, --month)
        }

        binding.plusMonthButton.setOnClickListener {
            setupDate(year, ++month)
        }

        binding.plusYearButton.setOnClickListener {
            setupDate(++year, month)
        }
    }

    private fun setupDate(year: Int, month: Int) {
        binding.dateFormat.text = getString(R.string.schedule_date_format, year, month)
        fetchAnniversarySchedules()
    }

    private fun fetchMySchedules() {
        // impl
        Log.d("MonthlyScheduleFragment", "내가 속한 밍글들의 스케줄 불러오기 실행")
    }

    private fun fetchSchedules(mingleId: Int) {
        Log.d("MonthlyScheduleFragment", "$mingleId 밍글의 스케줄 불러오기 실행")

        viewLifecycleOwner.lifecycleScope.launch {
            scheduleRepository.getMonthlySchedules(mingleId, year, month)
                .onSuccess { response ->
                    Log.d("MonthlyScheduleFragment", "${mingleId}번 밍글의 ${year}년도 ${month}월 일정 가져오기 성공")

                }
                .onError {
                    Log.d("MonthlyScheduleFragment", "${mingleId}번 밍글의 ${year}년도 ${month}월 일정 가져오는 중 에러 발생")
                }
                .onException {
                    Log.d("MonthlyScheduleFragment", "${mingleId}번 밍글의 ${year}년도 ${month}월 일정 가져오는 중 예외 발생 - $it")
                }
        }
    }

    private fun fetchAnniversarySchedules() {
        Log.d("MonthlyScheduleFragment", "특일 스케줄 불러오기 실행")

        viewLifecycleOwner.lifecycleScope.launch {
            anniversaryRepository.getAnniversary(year, month)
                .onSuccess { response ->
                    Log.d("MonthlyScheduleFragment", "특일의 ${year}년도 ${month}월 일정 가져오기 성공")
                    // impl
                }
                .onError {
                    Log.d("MonthlyScheduleFragment", "특일의 ${year}년도 ${month}월 일정 가져오는 중 에러 발생")
                }
                .onException {
                    Log.d("MonthlyScheduleFragment", "특일의 ${year}년도 ${month}월 일정 가져오는 중 예외 발생 - $it")
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}