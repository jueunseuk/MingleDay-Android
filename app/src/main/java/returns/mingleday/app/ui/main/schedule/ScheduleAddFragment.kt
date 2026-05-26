package returns.mingleday.app.ui.main.schedule

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import returns.mingleday.app.ui.main.MainActivity
import returns.mingleday.databinding.FragmentScheduleAddBinding
import java.time.LocalDateTime

class ScheduleAddFragment : Fragment() {

    private var _binding: FragmentScheduleAddBinding? = null
    private val binding get() = _binding!!
    private val mingleRepository = MingleRepository()
    private val categoryRepository = CategoryRepository()
    private val scheduleRepository = ScheduleRepository()
    private lateinit var myMingleAdapter : MyMingleAdapter
    private lateinit var mingleMemberAdapter : MingleMemberAdapter
    private lateinit var mingleCategoryAdapter : MingleCategoryAdapter

    private val scheduleMembers: List<ScheduleMemberRequest> = mutableListOf(
        ScheduleMemberRequest(1, "")
    )
    private var createScheduleRequest = CreateScheduleRequest(
        -1,
        -1,
        "",
        "",
        "",
        -1,
        true,
        true,
        true,
        true,
        LocalDateTime.now(),
        LocalDateTime.now(),
        scheduleMembers,
        RepeatType.DAILY,
        "",
        EndType.COUNT,
        ""
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScheduleAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).setToolbarTitle(R.string.add_mingle_title)
        setupRecyclerView()
        setupFetchMyMingleList()
        setupFetchCategoryList()
        setupFetchMingleMemberList()
        setupValidation()
        setupBackButton()
        setupToggles()
        setupAddScheduleButton()
    }

    private fun setupAddScheduleButton() {
        binding.addScheduleButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                scheduleRepository.createSchedule(createScheduleRequest.mingleId, createScheduleRequest)
                    .onSuccess { response ->
                        Log.d("ScheduleAddFragment", "스케줄 생성 성공")
                    }
                    .onError {
                        Log.d("ScheduleAddFragment", R.string.internal_server_error.toString())
                    }
                    .onException {
                        Log.d("ScheduleAddFragment", "스케줄 생성 중 예외 발생 - $it")
                    }
            }
        }
    }

    private fun setupRecyclerView() {
        // my mingle adapter
        myMingleAdapter = MyMingleAdapter { mingle ->
            createScheduleRequest.mingleId = mingle.mingleId
            setupFetchCategoryList()
            setupFetchMingleMemberList()
        }
        binding.mingleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.mingleRecyclerView.adapter = myMingleAdapter

        // mingle member adapter
        binding.mingleMemberRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.mingleMemberRecyclerView.adapter = mingleMemberAdapter

        // mingle category adapter
        binding.mingleCategoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.mingleCategoryRecyclerView.adapter = mingleCategoryAdapter
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
                    mingleMemberAdapter.submitList(response)
                }
                .onError {
                    Log.d("ScheduleAddFragment", R.string.internal_server_error.toString())
                }
                .onException {
                    Log.d("ScheduleAddFragment", "선택한 밍글의 멤버를 불러오는 중 예외 발생 - $it")
                }
        }
    }

    private fun setupToggles() {
        binding.repeatFalseButton.setOnClickListener {
            createScheduleRequest.isRepeated = true
            binding.repeatFalseButton.isSelected = true
            binding.repeatTrueButton.isSelected = false
        }
        binding.repeatTrueButton.setOnClickListener {
            createScheduleRequest.isRepeated = false
            binding.repeatFalseButton.isSelected = false
            binding.repeatTrueButton.isSelected = true
        }

        binding.repeatDailyButton.setOnClickListener {
            selectRepeatType(RepeatType.DAILY, it)
        }
        binding.repeatWeeklyButton.setOnClickListener {
            selectRepeatType(RepeatType.WEEKLY, it)
        }
        binding.repeatMonthlyButton.setOnClickListener {
            selectRepeatType(RepeatType.MONTHLY, it)
        }
        binding.repeatCustomButton.setOnClickListener {
            selectRepeatType(RepeatType.INTERVAL, it)
        }

        binding.repeatCountButton.setOnClickListener {
            createScheduleRequest.endType = EndType.COUNT
            binding.repeatCountButton.isSelected = true
            binding.repeatEndButton.isSelected = false
        }
        binding.repeatCountButton.setOnClickListener {
            createScheduleRequest.endType = EndType.DATE
            binding.repeatCountButton.isSelected = false
            binding.repeatEndButton.isSelected = true
        }

        binding.toggleAllDayValue.setOnClickListener {
            createScheduleRequest.isAllDay = !createScheduleRequest.isAllDay
            setToggleImage(binding.toggleAllDayValue, createScheduleRequest.isAllDay)
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
            it.isSelected = (it == selectedButton)
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

    private fun setupValidation() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val name = binding.inputScheduleNameValue.length() in 1..15
                val start = true // 조건식 추가
                val end = true // 조건식 추가

                binding.addScheduleButton.isEnabled = name && start && end
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.inputScheduleNameValue.addTextChangedListener(watcher)
    }
}