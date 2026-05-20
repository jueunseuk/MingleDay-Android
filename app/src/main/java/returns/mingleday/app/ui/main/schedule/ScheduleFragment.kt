package returns.mingleday.app.ui.main.schedule

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import returns.mingleday.R
import returns.mingleday.app.ui.main.MainActivity
import returns.mingleday.databinding.FragmentScheduleBinding

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private var mingleId: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).setToolbarTitle(R.string.my_schedule_title)
        mingleId = arguments?.getInt("mingleId") ?: -1

        if (mingleId == -1) {
            fetchMySchedules()
        } else {
            fetchSchedules(mingleId)
        }
    }

    private fun fetchMySchedules() {
        // impl
        Log.d("ScheduleFragment", "내가 속한 밍글들의 스케줄 불러오기 실행")
    }

    private fun fetchSchedules(mingleId: Int) {
        // impl
        Log.d("ScheduleFragment", "$mingleId 밍글의 스케줄 불러오기 실행")
    }
}