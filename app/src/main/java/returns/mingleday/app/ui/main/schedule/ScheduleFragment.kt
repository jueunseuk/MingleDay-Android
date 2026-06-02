package returns.mingleday.app.ui.main.schedule

import android.graphics.drawable.GradientDrawable
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
import returns.mingleday.app.data.remote.model.schedule.DetailScheduleResponse
import returns.mingleday.app.data.remote.model.schedule.ScheduleStatus
import returns.mingleday.app.data.remote.network.onError
import returns.mingleday.app.data.remote.network.onException
import returns.mingleday.app.data.remote.network.onSuccess
import returns.mingleday.app.data.repository.ScheduleRepository
import returns.mingleday.app.ui.main.MainActivity
import returns.mingleday.app.util.ColorUtil
import returns.mingleday.app.util.DateFormatter
import returns.mingleday.app.util.DateFormatter.formatCustom
import returns.mingleday.databinding.FragmentScheduleBinding
import java.time.LocalDateTime

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private val scheduleRepository = ScheduleRepository()
    private lateinit var scheduleMemberMemoAdapter: ScheduleMemberMemoAdapter

    private var mingleId: Int = -1
    private var scheduleId: Long = -1
    private var scheduleInstanceId: Long = -1
    private var scheduleName: String = "일정"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mingleId = arguments?.getInt("mingleId") ?: -1
        scheduleId = arguments?.getLong("scheduleId") ?: -1
        scheduleInstanceId = arguments?.getLong("scheduleInstanceId") ?: -1
        scheduleName = arguments?.getString("scheduleName") ?: "일정"

        val mainActivity = requireActivity() as MainActivity
        mainActivity.setToolbarTitle(scheduleName)

        setupRecyclerView()
        setupBackButton()
        fetchScheduleInstance()
        setupDeleteButton()
    }

    private fun setupDeleteButton() {
        binding.deleteButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                scheduleRepository.deleteSchedule(mingleId, scheduleId)
                    .onSuccess {
                        Log.d("ScheduleFragment", "${scheduleId}번 일정 삭제 성공")
                        Toast.makeText(requireContext(), "일정 삭제 완료", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }
                    .onError {
                        Log.d("ScheduleFragment", "${scheduleId}번 일정 삭제하는 중 에러 발생 - $it")
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                    .onException {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun setupRecyclerView() {
        scheduleMemberMemoAdapter = ScheduleMemberMemoAdapter()

        binding.memberRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = scheduleMemberMemoAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun fetchScheduleInstance() {
        viewLifecycleOwner.lifecycleScope.launch {
            scheduleRepository.getScheduleDetail(mingleId, scheduleInstanceId)
                .onSuccess { response ->
                    Log.d("ScheduleFragment", "${scheduleInstanceId}번 일정 인스턴스 가져오기 성공")
                    Log.d("ScheduleFragment", "$response")
                    bindSchedule(response)
                }
                .onError {
                    Log.d("ScheduleFragment", "${scheduleInstanceId}번 일정 인스턴스 가져오는 중 에러 발생 - $it")
                }
                .onException {
                    Log.d("ScheduleFragment", "${scheduleInstanceId}번 일정 인스턴스 가져오는 중 예외 발생 - $it")
                }
        }
    }

    private fun bindSchedule(response: DetailScheduleResponse) {
        scheduleName = response.title
        (requireActivity() as MainActivity).setToolbarTitle(response.title)

        binding.scheduleContentValue.text = response.content.ifBlank { "내용 없음" }
        binding.scheduleLocationValue.text = response.location.ifBlank { "장소 없음" }
        binding.instanceMemoValue.text = response.scheduleInstance.memo.ifBlank { "메모 없음" }

        binding.categoryValue.text = response.category.name
        binding.categoryValue.setTextColor(
            ColorUtil.getColorInt(response.category.textColor)
        )

        val categoryBackground = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 20f
            setColor(ColorUtil.getColorInt(response.category.backgroundColor))
        }

        binding.categoryValue.background = categoryBackground

        binding.scheduleTimeValue.text = makeTimeValue(
            response.scheduleInstance.startAt,
            response.scheduleInstance.endAt
        )

        binding.scheduleOptionValue.text = makeOptionText(response)

        scheduleMemberMemoAdapter.submitList(response.members)

        binding.memberLayout.visibility = if (response.members.isEmpty()) View.GONE else View.VISIBLE
        binding.contentLayout.visibility = if (response.content.isBlank()) View.GONE else View.VISIBLE
        binding.locationLayout.visibility = if (response.location.isBlank()) View.GONE else View.VISIBLE
        binding.instanceMemoLayout.visibility = if (response.scheduleInstance.memo.isBlank()) View.GONE else View.VISIBLE

        // make complete
        if(response.scheduleInstance.scheduleStatus == ScheduleStatus.TODO) {
            binding.completeScheduleButton.visibility = View.VISIBLE
        }

        // prev or next
        val hasPrev = response.scheduleInstance.prev == null
        val hasNext = response.scheduleInstance.next == null
        binding.repeatNavigationLayout.visibility = if (hasPrev || hasNext) View.VISIBLE else View.GONE
        binding.prevInstanceButton.isEnabled = hasPrev
        binding.nextInstanceButton.isEnabled = hasNext
        binding.prevInstanceButton.alpha = if (hasPrev) 1f else 0.4f
        binding.nextInstanceButton.alpha = if (hasNext) 1f else 0.4f
        binding.prevInstanceButton.setOnClickListener {
            response.scheduleInstance.prev?.let {
                scheduleInstanceId = it.scheduleInstanceId
                fetchScheduleInstance()
            }
        }
        binding.nextInstanceButton.setOnClickListener {
            response.scheduleInstance.next?.let {
                scheduleInstanceId = it.scheduleInstanceId
                fetchScheduleInstance()
            }
        }
    }

    private fun makeOptionText(response: DetailScheduleResponse): String {
        val options = mutableListOf<String>()

        options.add(if (response.isRepeated) "반복 일정" else "반복 없음")
        options.add(if (response.isPrivate) "비공개" else "공개")
        options.add(if (response.isLocked) "수정 잠금" else "수정 가능")

        return options.joinToString(" · ")
    }

    private fun makeTimeValue(startStr: String, endStr: String): String {
        val start = LocalDateTime.parse(startStr)
        val end = LocalDateTime.parse(endStr)

        return if (DateFormatter.isSameDay(start, end)) {
            if (DateFormatter.isStartOfDay(start) && DateFormatter.isEndOfDay(end)) {
                "하루종일"
            } else {
                start.formatCustom(1) + " " + start.formatCustom(8) + " ~ " + end.formatCustom(8)
            }
        } else {
            if (DateFormatter.isStartOfDay(start) && DateFormatter.isEndOfDay(end)) {
                start.formatCustom(1) + " ~ " + end.formatCustom(1) + " 하루종일"
            } else {
                start.formatCustom(1) + " " + start.formatCustom(8) + " ~ " +
                        end.formatCustom(1) + " " + end.formatCustom(8)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}