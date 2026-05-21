package returns.mingleday.app.ui.main.schedule

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import returns.mingleday.R
import returns.mingleday.app.data.remote.model.schedule.CreateScheduleRequest
import returns.mingleday.app.data.remote.model.schedule.EndType
import returns.mingleday.app.data.remote.model.schedule.RepeatType
import returns.mingleday.app.data.remote.model.schedule.ScheduleMemberRequest
import returns.mingleday.app.data.repository.ScheduleRepository
import returns.mingleday.app.ui.main.MainActivity
import returns.mingleday.databinding.FragmentScheduleAddBinding
import java.time.LocalDateTime

class ScheduleAddFragment : Fragment() {

    private var _binding: FragmentScheduleAddBinding? = null
    private val binding get() = _binding!!
    private val scheduleRepository = ScheduleRepository()

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
        setupFetchMingleList()
        setupFetchCategoryList()
        setupFetchMingleMemberList()
        setupValidation()
        setupBackButton()
        setupToggles()
    }

    private fun setupFetchMingleList() {
        TODO("Not yet implemented")
    }

    private fun setupFetchCategoryList() {
        TODO("Not yet implemented")
    }

    private fun setupFetchMingleMemberList() {
        TODO("Not yet implemented")
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