package returns.mingleday.app.ui.main.schedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import returns.mingleday.R
import returns.mingleday.app.data.remote.model.schedule.CreateScheduleRequest
import returns.mingleday.app.data.remote.model.schedule.EndType
import returns.mingleday.app.data.remote.model.schedule.RepeatType
import returns.mingleday.app.data.remote.model.schedule.ScheduleMemberRequest
import returns.mingleday.app.data.remote.network.onError
import returns.mingleday.app.data.remote.network.onException
import returns.mingleday.app.data.remote.network.onSuccess
import returns.mingleday.app.data.repository.CategoryRepository
import returns.mingleday.app.data.repository.MingleRepository
import returns.mingleday.app.data.repository.ScheduleRepository
import returns.mingleday.app.ui.common.SpaceItemDecoration
import returns.mingleday.app.ui.main.MainActivity
import returns.mingleday.databinding.FragmentScheduleAddBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ScheduleAddFragment : Fragment() {

    private var _binding: FragmentScheduleAddBinding? = null
    private val binding get() = _binding!!

    private val mingleRepository = MingleRepository()
    private val categoryRepository = CategoryRepository()
    private val scheduleRepository = ScheduleRepository()

    private lateinit var myMingleAdapter: MyMingleAdapter
    private lateinit var mingleMemberAdapter: MingleMemberAdapter
    private lateinit var mingleCategoryAdapter: MingleCategoryAdapter

    private val scheduleMembers = mutableListOf<ScheduleMemberRequest>()
    private var selectedWeekOfDay = mutableListOf<Int>()

    private var startDate: LocalDate = LocalDate.now()
    private var endDate: LocalDate = LocalDate.now()
    private var startTime: LocalTime? = null
    private var endTime: LocalTime? = null
    private var endDateCondition: LocalDate = LocalDate.now().plusDays(14)

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    private var createScheduleRequest = CreateScheduleRequest(
        -1,
        -1,
        "",
        "",
        "",
        -1,
        false,
        false,
        false,
        true,
        LocalDate.now().atStartOfDay().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        LocalDate.now().atStartOfDay().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        scheduleMembers,
        RepeatType.DAILY,
        "",
        EndType.COUNT,
        ""
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).setToolbarTitle(R.string.add_schedule_title)
        createScheduleRequest.mingleId = arguments?.getInt("mingleId") ?: -1
        Log.d("ScheduleAddFragment", "전달 받은 mingleId = ${createScheduleRequest.mingleId}")

        setupRecyclerView()
        setupFetchMyMingleList()

        if (createScheduleRequest.mingleId != -1) {
            setupFetchCategoryList()
            setupFetchMingleMemberList()
        }

        setupDateTimeInput()
        setupValidation()
        setupBackButton()
        setupToggles()
        setupAddScheduleButton()
        setupDaysOfWeek()
    }

    private fun setupDateTimeInput() {
        updateDateText()
        updateTimeText()
        applyDateTimeToRequest()

        binding.startDateValue.setOnClickListener {
            showDatePicker(startDate) { selectedDate ->
                startDate = selectedDate

                if (endDate.isBefore(startDate)) {
                    endDate = startDate
                }

                syncEndTimeIfNeeded()
                updateDateText()
                updateTimeText()
                applyDateTimeToRequest()
                validateInput()
            }
        }

        binding.endDateValue.setOnClickListener {
            showDatePicker(endDate) { selectedDate ->
                endDate = selectedDate

                if (endDate.isBefore(startDate)) {
                    startDate = endDate
                }

                syncEndTimeIfNeeded()
                updateDateText()
                updateTimeText()
                applyDateTimeToRequest()
                validateInput()
            }
        }

        binding.startTimeValue.setOnClickListener {
            val defaultStartTime = LocalTime.now()
                .plusHours(1)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)

            showTimePicker(startTime ?: defaultStartTime) { selectedTime ->
                startTime = selectedTime.withSecond(0).withNano(0)

                syncEndTimeIfNeeded()
                updateDateText()
                updateTimeText()
                applyDateTimeToRequest()
                validateInput()
            }
        }

        binding.endTimeValue.setOnClickListener {
            val defaultStartTime = LocalTime.now()
                .plusHours(1)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
            val defaultEndTime = defaultStartTime.plusHours(1)

            showTimePicker(
                endTime ?: startTime?.plusHours(1) ?: defaultEndTime
            ) { selectedTime ->

                endTime = selectedTime.withSecond(0).withNano(0)

                syncEndTimeIfNeeded()
                updateDateText()
                updateTimeText()
                applyDateTimeToRequest()
                validateInput()
            }
        }

        // 종료 날짜 조건을 위한 선택기
        binding.repeatEndValue.setOnClickListener {
            showDatePicker(endDateCondition) { selectedDate ->
                endDateCondition = selectedDate

                updateDateText()
                applyDateTimeToRequest()
                validateInput()
            }
        }
    }

    private fun syncEndTimeIfNeeded() {
        if (endDate.isBefore(startDate)) {
            endDate = startDate
        }

        if (
            createScheduleRequest.isRepeated &&
            createScheduleRequest.repeatType in listOf(
                RepeatType.DAILY,
                RepeatType.WEEKLY,
                RepeatType.MONTHLY
            )
        ) {
            endDate = startDate
        }

        if (createScheduleRequest.isAllDay) return
        if (endDate.isAfter(startDate)) return

        val start = startTime ?: return
        val end = endTime

        if (end == null || end.isBefore(start)) {
            endTime = start
        }
    }

    private fun updateDateText() {
        binding.startDateValue.text = startDate.format(dateFormatter)
        binding.endDateValue.text = endDate.format(dateFormatter)
        binding.repeatEndValue.text = endDateCondition.format(dateFormatter)
    }

    private fun updateTimeText() {
        binding.startTimeValue.text = startTime?.format(timeFormatter) ?: "시작 시간"
        binding.endTimeValue.text = endTime?.format(timeFormatter) ?: "종료 시간"
    }

    private fun showDatePicker(
        initialDate: LocalDate,
        onDateSelected: (LocalDate) -> Unit
    ) {
        DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                onDateSelected(
                    LocalDate.of(
                        selectedYear,
                        selectedMonth + 1,
                        selectedDay
                    )
                )
            },
            initialDate.year,
            initialDate.monthValue - 1,
            initialDate.dayOfMonth
        ).show()
    }

    private fun showTimePicker(
        initialTime: LocalTime,
        onTimeSelected: (LocalTime) -> Unit
    ) {
        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                onTimeSelected(LocalTime.of(hour, minute))
            },
            initialTime.hour,
            initialTime.minute,
            true
        ).show()
    }

    private fun applyDateTimeToRequest() {
        val startDateTime: LocalDateTime
        val endDateTime: LocalDateTime

        if (createScheduleRequest.isAllDay) {
            startDateTime = startDate.atStartOfDay()
            endDateTime = endDate.atStartOfDay()
        } else {
            startDateTime = LocalDateTime.of(
                startDate,
                startTime ?: LocalTime.of(0, 0)
            )

            endDateTime = LocalDateTime.of(
                endDate,
                endTime ?: startTime ?: LocalTime.of(0, 0)
            )
        }

        createScheduleRequest.startAt = startDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        createScheduleRequest.endAt = endDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    private fun setupRecyclerView() {
        // my mingle adapter
        myMingleAdapter = MyMingleAdapter { mingle ->
            createScheduleRequest.mingleId = mingle.mingleId
            createScheduleRequest.categoryId = -1
            scheduleMembers.clear()
            createScheduleRequest.mingleMembers = scheduleMembers

            myMingleAdapter.setSelectedMingleId(mingle.mingleId)
            setupFetchCategoryList()
            setupFetchMingleMemberList()
            validateInput()

            Toast.makeText(
                requireContext(),
                "${mingle.mingleName}을(를) 선택했습니다.",
                Toast.LENGTH_SHORT
            ).show()
        }
        myMingleAdapter.setSelectedMingleId(createScheduleRequest.mingleId)
        binding.mingleRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.mingleRecyclerView.addItemDecoration(SpaceItemDecoration(16))
        binding.mingleRecyclerView.adapter = myMingleAdapter

        // mingle member adapter
        mingleMemberAdapter = MingleMemberAdapter { member ->
            val existing = scheduleMembers.find { it.mingleMemberId == member.memberId }

            if (existing != null) {
                scheduleMembers.remove(existing)
                Log.d("ScheduleAddFragment", "밍글 멤버 ${member.name} 선택 해제")
            } else {
                scheduleMembers.add(
                    ScheduleMemberRequest(
                        member.memberId,
                        "해당 멤버가 알아야할 내용을 입력해주세요."
                    )
                )
                validateInput()
                Log.d("ScheduleAddFragment", "밍글 멤버 ${member.name} 선택")
            }
        }
        binding.mingleMemberRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.mingleMemberRecyclerView.addItemDecoration(SpaceItemDecoration(16))
        binding.mingleMemberRecyclerView.adapter = mingleMemberAdapter

        // mingle category adapter
        mingleCategoryAdapter = MingleCategoryAdapter { category ->
            createScheduleRequest.categoryId = category.categoryId
            validateInput()
            Log.d("ScheduleAddFragment", "카테고리 ${category.name} 선택")
        }
        binding.mingleCategoryRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.mingleCategoryRecyclerView.addItemDecoration(SpaceItemDecoration(16))
        binding.mingleCategoryRecyclerView.adapter = mingleCategoryAdapter
    }

    private fun setupToggles() {
        // 초기 반복 여부는 false
        binding.repeatFalseButton.isSelected = true
        binding.repeatTrueButton.isSelected = false
        binding.repeatComponent.visibility = View.GONE

        // 반복하지 않음 선택
        binding.repeatFalseButton.setOnClickListener {
            clickNotUseRepeat()
        }

        // 반복 선택
        binding.repeatTrueButton.setOnClickListener {
            clickUseRepeat()
        }

        // 종료 조건으로 날짜 설정은 daily repeat만 가능
        binding.repeatDailyButton.setOnClickListener {
            clickDailyRepeat(it)
        }
        binding.repeatWeeklyButton.setOnClickListener {
            clickWeeklyRepeat(it)
        }
        binding.repeatMonthlyButton.setOnClickListener {
            clickMonthlyRepeat(it)
        }
        binding.repeatCustomButton.setOnClickListener {
            clickIntervalRepeat(it)
        }

        binding.repeatCountButton.setOnClickListener {
            clickCount()
        }
        binding.repeatEndButton.setOnClickListener {
            clickEnd()
        }

        binding.timeContainer.visibility = View.GONE
        setToggleImage(binding.toggleAllDayValue, createScheduleRequest.isAllDay)
        binding.toggleAllDayValue.setOnClickListener {
            createScheduleRequest.isAllDay = !createScheduleRequest.isAllDay
            setToggleImage(binding.toggleAllDayValue, createScheduleRequest.isAllDay)

            if (createScheduleRequest.isAllDay) {
                binding.timeContainer.visibility = View.GONE
                startTime = null
                endTime = null
            } else {
                val defaultStartTime = LocalTime.now()
                    .plusHours(1)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0)
                binding.timeContainer.visibility = View.VISIBLE
                startTime = defaultStartTime
                endTime = defaultStartTime.plusHours(1)
            }

            updateTimeText()
            applyDateTimeToRequest()
            validateInput()
        }

        binding.togglePrivateValue.setOnClickListener {
            createScheduleRequest.isPrivate = !createScheduleRequest.isPrivate
            setToggleImage(binding.togglePrivateValue, createScheduleRequest.isPrivate)
        }

        binding.toggleLockValue.setOnClickListener {
            createScheduleRequest.isLocked = !createScheduleRequest.isLocked
            setToggleImage(binding.toggleLockValue, createScheduleRequest.isLocked)
        }
    }

    private fun selectRepeatType(
        type: RepeatType,
        selectedButton: View
    ) {
        createScheduleRequest.repeatType = type

        listOf(
            binding.repeatDailyButton,
            binding.repeatWeeklyButton,
            binding.repeatMonthlyButton,
            binding.repeatCustomButton
        ).forEach {
            it.isSelected = it == selectedButton
        }

        binding.weeklyPicker.visibility = if (type == RepeatType.WEEKLY) View.VISIBLE else View.GONE
        binding.monthlyPicker.visibility = if (type == RepeatType.MONTHLY) View.VISIBLE else View.GONE
        binding.intervalPicker.visibility = if (type == RepeatType.INTERVAL) View.VISIBLE else View.GONE

        syncEndTimeIfNeeded()
        updateDateText()
        updateTimeText()
        applyDateTimeToRequest()
        validateInput()
    }

    private fun setToggleImage(imageView: ImageView, isOn: Boolean) {
        imageView.setImageResource(
            if (isOn) R.drawable.ic_toggle_on else R.drawable.ic_toggle_off
        )
    }

    private fun setupValidation() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateInput()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.inputScheduleNameValue.addTextChangedListener(watcher)
        validateInput()
    }

    private fun validateInput() {
        val isNameValid = binding.inputScheduleNameValue.length() in 1..30
        val isMingleValid = createScheduleRequest.mingleId != -1
        val isDateRangeValid = !endDate.isBefore(startDate)
        val isDayOfWeekValid = createScheduleRequest.repeatType != RepeatType.WEEKLY || selectedWeekOfDay.isNotEmpty()
        val isMonthlyValueValid = createScheduleRequest.repeatType != RepeatType.MONTHLY || binding.monthlyPicker.text.isNotEmpty()
        val isIntervalValueValid = createScheduleRequest.repeatType != RepeatType.INTERVAL || binding.intervalPicker.text.isDigitsOnly()
        val isCountValidValid = createScheduleRequest.endType != EndType.COUNT || binding.repeatCountValue.text.isDigitsOnly()
        val isEndDateValid = if (
            createScheduleRequest.isRepeated &&
            createScheduleRequest.endType == EndType.DATE
        ) {
            !endDateCondition.isBefore(LocalDate.now())
        } else {
            true
        }

        val isTimeRangeValid = if (createScheduleRequest.isAllDay) {
            true
        } else {
            val start = LocalDateTime.of(startDate, startTime ?: LocalTime.of(0, 0))
            val end = LocalDateTime.of(endDate, endTime ?: startTime ?: LocalTime.of(0, 0))
            !end.isBefore(start)
        }

        binding.addScheduleButton.isEnabled =   isNameValid &&
                                                isMingleValid &&
                                                isDateRangeValid &&
                                                isDayOfWeekValid &&
                                                isMonthlyValueValid &&
                                                isIntervalValueValid &&
                                                isCountValidValid &&
                                                isTimeRangeValid &&
                                                isEndDateValid &&
                                                scheduleMembers.isNotEmpty() &&
                                                createScheduleRequest.categoryId != -1L
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupFetchMyMingleList() {
        viewLifecycleOwner.lifecycleScope.launch {
            mingleRepository.getMyMinglesSimple()
                .onSuccess { response ->
                    Log.d("ScheduleAddFragment", "내가 참여중인 밍글 가져오기 성공")
                    myMingleAdapter.submitList(response)
                }
                .onError {
                    Log.d("ScheduleAddFragment", R.string.internal_server_error.toString())
                }
                .onException {
                    Log.d("ScheduleAddFragment", "단순 내 밍글 목록을 가져오는 중 예외 발생 - $it")
                }
        }
    }

    private fun setupFetchCategoryList() {
        viewLifecycleOwner.lifecycleScope.launch {
            categoryRepository.getMingleCategory(createScheduleRequest.mingleId)
                .onSuccess { response ->
                    Log.d("ScheduleAddFragment", "선택한 밍글의 카테고리 불러오기 성공")
                    binding.mingleCategoryRecyclerView.visibility = View.VISIBLE
                    binding.mingleCategoryAlternativeText.visibility = View.GONE
                    mingleCategoryAdapter.submitList(response)
                }
                .onError {
                    Log.d("ScheduleAddFragment", R.string.internal_server_error.toString())
                }
                .onException {
                    Log.d("ScheduleAddFragment", "선택한 밍글의 카테고리를 불러오는 중 예외 발생 - $it")
                }
        }
    }

    private fun setupFetchMingleMemberList() {
        viewLifecycleOwner.lifecycleScope.launch {
            mingleRepository.getMingleMembers(createScheduleRequest.mingleId)
                .onSuccess { response ->
                    Log.d("ScheduleAddFragment", "선택한 밍글의 멤버 불러오기 성공")
                    binding.mingleMemberRecyclerView.visibility = View.VISIBLE
                    binding.mingleMemberAlternativeText.visibility = View.GONE
                    mingleMemberAdapter.submitList(response)
                }
                .onError {
                    Log.d("ScheduleAddFragment", "선택한 밍글의 멤버를 불러오는 중 오류 발생 - $it")
                }
                .onException {
                    Log.d("ScheduleAddFragment", "선택한 밍글의 멤버를 불러오는 중 예외 발생 - $it")
                }
        }
    }

    private fun setupAddScheduleButton() {
        binding.addScheduleButton.setOnClickListener {
            createScheduleRequest.title = binding.inputScheduleNameValue.text.toString().trim()
            createScheduleRequest.content = binding.inputScheduleContentValue.text.toString().trim()
            createScheduleRequest.location = binding.inputScheduleLocationValue.text.toString().trim()
            createScheduleRequest.endValue = endDateCondition.format(dateFormatter)
            createScheduleRequest.repeatValue = when(createScheduleRequest.repeatType) {
                RepeatType.WEEKLY -> selectedWeekOfDay.sorted().joinToString(",")
                RepeatType.MONTHLY -> binding.monthlyPicker.text.toString().trim()
                RepeatType.INTERVAL -> binding.intervalPicker.text.toString().trim()
                else -> ""
            }
            applyDateTimeToRequest()

            viewLifecycleOwner.lifecycleScope.launch {
                scheduleRepository.createSchedule(
                    createScheduleRequest.mingleId,
                    createScheduleRequest
                )
                    .onSuccess { response ->
                        Toast.makeText(requireContext(), "일정을 추가했습니다.", Toast.LENGTH_SHORT).show()
                        Log.d("ScheduleAddFragment", "스케줄 생성 성공")

                        val fragment = ScheduleFragment().apply {
                            arguments = Bundle().apply {
                                putInt("mingleId", createScheduleRequest.mingleId)
                                putLong("scheduleId", response.scheduleId)
                                putLong("scheduleInstanceId", response.scheduleInstance.scheduleInstanceId)
                                putString("scheduleName", response.title)
                            }
                        }

                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.main_frame, fragment)
                            .addToBackStack(null)
                            .commit()
                    }
                    .onError {
                        Toast.makeText(requireContext(), "일정을 추가하는 중 오류 발생 - $it.", Toast.LENGTH_SHORT).show()
                        Log.d("ScheduleAddFragment", R.string.internal_server_error.toString())
                    }
                    .onException {
                        Toast.makeText(requireContext(), "일정을 추가하는 중 예외 발생 - $it", Toast.LENGTH_SHORT).show()
                        Log.d("ScheduleAddFragment", "스케줄 생성 중 예외 발생 - $it")
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // click use repeat button
    private fun clickUseRepeat() {
        createScheduleRequest.isRepeated = true
        binding.repeatFalseButton.isSelected = false
        binding.repeatTrueButton.isSelected = true
        binding.repeatComponent.visibility = View.VISIBLE
        binding.addScheduleRepeatHint.visibility = View.VISIBLE

        clickDailyRepeat(binding.repeatDailyButton)
        clickCount()
    }

    private fun clickNotUseRepeat() {
        createScheduleRequest.isRepeated = false
        binding.repeatFalseButton.isSelected = true
        binding.repeatTrueButton.isSelected = false
        binding.repeatComponent.visibility = View.GONE
        binding.addScheduleRepeatHint.visibility = View.GONE
    }

    // click repeat method
    // count 버튼은 항상 유지
    // end 버튼은 daily인지에 따라 visibility 달라짐
    private fun clickDailyRepeat(selectedButton: View) {
        selectRepeatType(RepeatType.DAILY, selectedButton)
        binding.repeatCountButton.isSelected = true
        binding.repeatEndButton.visibility = View.VISIBLE
        clickCount()
    }

    private fun clickWeeklyRepeat(selectedButton: View) {
        selectRepeatType(RepeatType.WEEKLY, selectedButton)
        binding.repeatEndButton.visibility = View.GONE
        clickCount()
    }

    private fun clickMonthlyRepeat(selectedButton: View) {
        selectRepeatType(RepeatType.MONTHLY, selectedButton)
        binding.repeatEndButton.visibility = View.GONE
        clickCount()
    }

    private fun clickIntervalRepeat(selectedButton: View) {
        selectRepeatType(RepeatType.INTERVAL, selectedButton)
        binding.repeatEndButton.visibility = View.GONE
        clickCount()
    }

    // click end condition type
    // clickEnd only for interval method
    private fun clickCount() {
        createScheduleRequest.endType = EndType.COUNT
        binding.repeatCountButton.isSelected = true
        binding.repeatEndButton.isSelected = false
        binding.repeatCountValue.visibility = View.VISIBLE
        binding.repeatEndValue.visibility = View.INVISIBLE
    }

    private fun clickEnd() {
        createScheduleRequest.endType = EndType.DATE
        binding.repeatCountButton.isSelected = false
        binding.repeatEndButton.isSelected = true
        binding.repeatCountValue.visibility = View.INVISIBLE
        binding.repeatEndValue.visibility = View.VISIBLE
    }

    // click days of week
    private fun setupDaysOfWeek() {
        binding.sunday.setOnClickListener { addOrRemove(0, binding.sunday) }
        binding.monday.setOnClickListener { addOrRemove(1, binding.monday) }
        binding.tuesday.setOnClickListener { addOrRemove(2, binding.tuesday) }
        binding.wednesday.setOnClickListener { addOrRemove(3, binding.wednesday) }
        binding.thursday.setOnClickListener { addOrRemove(4, binding.thursday) }
        binding.friday.setOnClickListener { addOrRemove(5, binding.friday) }
        binding.saturday.setOnClickListener { addOrRemove(6, binding.saturday) }
    }

    private fun addOrRemove(target: Int, button: View) {
        if(selectedWeekOfDay.contains(target)) {
            selectedWeekOfDay.remove(target)
            button.isSelected = false
        } else {
            selectedWeekOfDay.add(target)
            button.isSelected = true
        }
    }
}