package returns.mingleday.app.ui.main.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import returns.mingleday.R
import returns.mingleday.app.ui.main.MainActivity
import returns.mingleday.databinding.FragmentMonthlyScheduleBinding

class ScheduleFragment : Fragment() {

    private var _binding: FragmentMonthlyScheduleBinding? = null
    private val binding get() = _binding!!

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}