package returns.mingleday.app.ui.main.mingle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import returns.mingleday.R
import returns.mingleday.app.ui.main.MainActivity
import returns.mingleday.databinding.FragmentMingleListBinding

class MingleListFragment : Fragment() {

    private var _binding: FragmentMingleListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMingleListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).setToolbarTitle(R.string.mingle_list_title)
    }

    private fun setupFetchMingleList() {
        viewLifecycleOwner.lifecycleScope.launch {
            mingleRepository.getMyMingles()
                .onSuccess { mingles ->
                    binding.mingleCountText.text = "소속 밍글 ${mingles.size}개"
                    mingleAdapter.submitList(mingles)
                }
                .onError {
                    Toast.makeText(requireContext(), R.string.internal_server_error, Toast.LENGTH_LONG).show()
                }
                .onException {
                    Toast.makeText(requireContext(), R.string.internal_server_error, Toast.LENGTH_LONG).show()
                }
        }
    }
}