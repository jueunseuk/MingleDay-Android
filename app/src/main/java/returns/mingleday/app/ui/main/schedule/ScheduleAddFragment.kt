package returns.mingleday.app.ui.main.schedule

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

    private val scheduleMembers = mutableListOf<ScheduleMemberRequest>()
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
        false,
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

        createScheduleRequest.mingleId = arguments?.getInt("mingleId") ?: -1
        Log.d("ScheduleAddFragment", "전달 받은 mingleId = ${createScheduleRequest.mingleId}")

        (requireActivity() as MainActivity).setToolbarTitle(R.string.add_mingle_title)
        setupRecyclerView()
        setupFetchMyMingleList()
        if(createScheduleRequest.mingleId != -1) {
            setupFetchCategoryList()
            setupFetchMingleMemberList()
        }
        setupValidation()
        setupBackButton()
        setupToggles()
        setupAddScheduleButton()
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
            Toast.makeText(requireContext(), "${mingle.mingleName}을(를) 선택했습니다.", Toast.LENGTH_SHORT).show()
        }
        myMingleAdapter.setSelectedMingleId(createScheduleRequest.mingleId)
        binding.mingleRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.mingleRecyclerView.addItemDecoration(
            SpaceItemDecoration(16)
        )
        binding.mingleRecyclerView.adapter = myMingleAdapter

        // mingle member adapter
        mingleMemberAdapter = MingleMemberAdapter { member ->
            val existing = scheduleMembers.find { it.mingleMemberId == member.memberId }

            if (existing != null) {
                scheduleMembers.remove(existing)
                Toast.makeText(requireContext(), "${member.name} 선택 해제", Toast.LENGTH_SHORT).show()
            } else {
                scheduleMembers.add(
                    ScheduleMemberRequest(
                        member.memberId,
                        ""
                    )
                )
                Toast.makeText(requireContext(), "${member.name} 선택", Toast.LENGTH_SHORT).show()
            }
        }
        binding.mingleMemberRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.mingleMemberRecyclerView.addItemDecoration(
            SpaceItemDecoration(16)
        )
        binding.mingleMemberRecyclerView.adapter = mingleMemberAdapter

        // mingle category adapter
        mingleCategoryAdapter = MingleCategoryAdapter{ category ->
            createScheduleRequest.categoryId = category.categoryId
            Log.d("ScheduleAddFragment", "카테고리 ${category.name} 선택")
        }
        binding.mingleCategoryRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.mingleCategoryRecyclerView.addItemDecoration(
            SpaceItemDecoration(16)
        )
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

    private fun setupToggles() {
        // repeat
        binding.repeatFalseButton.isSelected = true
        binding.repeatTrueButton.isSelected = false
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

        // repeat value
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

        // setting
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
}