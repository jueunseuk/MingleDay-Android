package returns.mingleday.app.ui.main.schedule

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import returns.mingleday.R
import returns.mingleday.app.data.remote.model.schedule.AnniversaryItemWithType
import returns.mingleday.app.data.remote.model.schedule.CalendarDayUiModel
import returns.mingleday.app.data.remote.model.schedule.CalendarScheduleUiModel
import returns.mingleday.app.data.remote.model.schedule.MonthlyScheduleResponse
import returns.mingleday.app.data.remote.network.onError
import returns.mingleday.app.data.remote.network.onException
import returns.mingleday.app.data.remote.network.onSuccess
import returns.mingleday.app.data.repository.AnniversaryRepository
import returns.mingleday.app.data.repository.CategoryRepository
import returns.mingleday.app.data.repository.ScheduleRepository
import returns.mingleday.app.ui.common.SpaceItemDecoration
import returns.mingleday.app.ui.main.MainActivity
import returns.mingleday.databinding.FragmentMonthlyScheduleBinding
import java.time.LocalDate

class MonthlyScheduleFragment : Fragment() {

    private var _binding: FragmentMonthlyScheduleBinding? = null
    private val binding get() = _binding!!
    private val scheduleRepository = ScheduleRepository()
    private val anniversaryRepository = AnniversaryRepository()
    private val categoryRepository = CategoryRepository()
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var scheduleCategoryAdapter: ScheduleCategoryAdapter
    private lateinit var dailyScheduleAdapter: DailyScheduleAdapter

    private var year: Int = LocalDate.now().year
    private var month: Int = LocalDate.now().month.value
    private var day: Int = LocalDate.now().dayOfMonth
    private var mingleId: Int = -1
    private var monthlySchedules: List<MonthlyScheduleResponse> = emptyList()
    private var anniversarySchedules: List<AnniversaryItemWithType> = emptyList()

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
            binding.categoryRecyclerView.visibility = View.GONE
        } else {
            fetchSchedules()
            binding.categoryRecyclerView.visibility = View.VISIBLE
            fetchMingleCategory()
        }
        fetchAnniversarySchedules()
        fetchDailySchedules()
    }

    private fun fetchMingleCategory() {
        viewLifecycleOwner.lifecycleScope.launch {
            categoryRepository.getMingleCategory(mingleId)
                .onSuccess { response ->
                    Log.d("MonthlyScheduleFragment", "${mingleId}번 밍글의 카테고리 가져오기 성공")
                    scheduleCategoryAdapter.submitList(response)
                }
                .onError {
                    Log.d("MonthlyScheduleFragment", "${mingleId}번 밍글의 카테고리 가져오는 중 에러 발생 - $it")
                }
                .onException {
                    Log.d("MonthlyScheduleFragment", "${mingleId}번 밍글의 카테고리 가져오는 중 예외 발생 - $it")
                }
        }
    }

    private fun setupCalendarRecyclerView() {
        // calendar recycler view
        setupDay(day)
        calendarAdapter = CalendarAdapter { day ->
            setupDay(day)
            fetchDailySchedules()
        }
        binding.calendarRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 7)
            adapter = calendarAdapter
            isNestedScrollingEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }
        calendarAdapter.submitList(createCalendarDays(year, month))

        // schedule category recycler view
        scheduleCategoryAdapter = ScheduleCategoryAdapter()
        binding.categoryRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.categoryRecyclerView.addItemDecoration(
            SpaceItemDecoration(8)
        )
        binding.categoryRecyclerView.adapter = scheduleCategoryAdapter

        // daily schedule recycler view
        dailyScheduleAdapter = DailyScheduleAdapter { item ->
            // 해당 스케줄로 이동
        }
        binding.dailyScheduleRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dailyScheduleAdapter
            isNestedScrollingEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }
        binding.dailyScheduleRecyclerView.addItemDecoration(
            SpaceItemDecoration(5)
        )
        binding.dailyScheduleRecyclerView.adapter = dailyScheduleAdapter
    }

    private fun fetchDailySchedules() {
        viewLifecycleOwner.lifecycleScope.launch {
            scheduleRepository.getDailySchedules(mingleId, year, month, day)
                .onSuccess { response ->
                    Log.d(
                        "MonthlyScheduleFragment",
                        "${year}년 ${month}월 ${day}일의 일정 목록 가져오기 성공"
                    )
                    dailyScheduleAdapter.submitList(response)
                }
                .onError {
                    Log.d(
                        "MonthlyScheduleFragment",
                        "${year}년 ${month}월 ${day}일의 일정 목록 가져오는 중 에러 발생 - $it"
                    )
                }
                .onException {
                    Log.d(
                        "MonthlyScheduleFragment",
                        "${year}년 ${month}월 ${day}일의 일정 목록 가져오는 중 예외 발생 - $it"
                    )
                }
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
        if(mingleId == -1) {
            fetchMySchedules()
        } else {
            fetchSchedules()
        }
    }

    private fun setupDay(day: Int) {
        this.day = day
        binding.dayFormat.text = getString(R.string.schedule_day_format, day)
    }

    private fun fetchMySchedules() {
        Log.d("MonthlyScheduleFragment", "내가 속한 밍글들의 스케줄 불러오기 실행")
        viewLifecycleOwner.lifecycleScope.launch {
            scheduleRepository.getMonthlySchedules(mingleId, year, month)
                .onSuccess { response ->
                    Log.d("MonthlyScheduleFragment", "내가 속한 ${year}년도 ${month}월 일정 가져오기 성공")
                    Log.d("MonthlyScheduleFragment", "$response")
                }
                .onError {
                    Log.d("MonthlyScheduleFragment", "내가 속한 ${year}년도 ${month}월 일정 가져오는 중 에러 발생 - $it")
                }
                .onException {
                    Log.d("MonthlyScheduleFragment", "내가 속한 ${year}년도 ${month}월 일정 가져오는 중 예외 발생 - $it")
                }
        }
    }

    private fun fetchSchedules() {
        Log.d("MonthlyScheduleFragment", "$mingleId 밍글의 스케줄 불러오기 실행")

        viewLifecycleOwner.lifecycleScope.launch {
            scheduleRepository.getMonthlySchedules(mingleId, year, month)
                .onSuccess { response ->
                    Log.d("MonthlyScheduleFragment", "${mingleId}번 밍글의 ${year}년도 ${month}월 일정 가져오기 성공")
                    Log.d("MonthlyScheduleFragment", "$response")

                    monthlySchedules = response
                    updateCalendar()
                }
                .onError {
                    Log.d("MonthlyScheduleFragment", "${mingleId}번 밍글의 ${year}년도 ${month}월 일정 가져오는 중 에러 발생 - $it")
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
                    anniversarySchedules = response
                    updateCalendar()
                }
                .onError {
                    Log.d("MonthlyScheduleFragment", "특일의 ${year}년도 ${month}월 일정 가져오는 중 에러 발생")
                }
                .onException {
                    Log.d("MonthlyScheduleFragment", "특일의 ${year}년도 ${month}월 일정 가져오는 중 예외 발생 - $it")
                }
        }
    }

    private fun updateCalendar() {
        if (!::calendarAdapter.isInitialized) return

        calendarAdapter.submitList(
            createCalendarDays(
                year = year,
                month = month,
                anniversaries = anniversarySchedules,
                schedules = monthlySchedules
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createCalendarDays(
        year: Int,
        month: Int,
        anniversaries: List<AnniversaryItemWithType> = emptyList(),
        schedules: List<MonthlyScheduleResponse> = emptyList()
    ): List<CalendarDayUiModel> {
        val result = mutableListOf<CalendarDayUiModel>()

        val firstDate = LocalDate.of(year, month, 1)
        val lastDay = firstDate.lengthOfMonth()
        val firstDayOfWeek = firstDate.dayOfWeek.value
        val emptyCount = if (firstDayOfWeek == 7) 0 else firstDayOfWeek
        val today = LocalDate.now()

        repeat(emptyCount) {
            result.add(CalendarDayUiModel(day = null))
        }

        for (day in 1..lastDay) {
            val date = LocalDate.of(year, month, day)

            val anniversary = anniversaries.find {
                it.locdate.takeLast(2).toIntOrNull() == day
            }

            val schedulesOfDay = schedules.filter { schedule ->
                val startDate = LocalDate.parse(schedule.scheduleInstance.startAt.substring(0, 10))
                val endDate = LocalDate.parse(schedule.scheduleInstance.endAt.substring(0, 10))

                !date.isBefore(startDate) && !date.isAfter(endDate)
            }.map { schedule ->
                CalendarScheduleUiModel(
                    title = schedule.title,
                    backgroundColor = schedule.category.backgroundColor,
                    textColor = schedule.category.textColor
                )
            }

            result.add(
                CalendarDayUiModel(
                    day = day,
                    schedules = schedulesOfDay,
                    dayOfWeek = date.dayOfWeek.value,
                    isToday = date == today,
                    anniversaryName = anniversary?.dateName,
                    isHoliday = anniversary?.isHoliday == true
                )
            )
        }

        return result
    }
}