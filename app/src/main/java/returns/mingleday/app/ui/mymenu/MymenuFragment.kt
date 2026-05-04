package returns.mingleday.app.ui.mymenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import returns.mingleday.R
import returns.mingleday.app.MingleDayApplication
import returns.mingleday.databinding.FragmentMymenuBinding


class MymenuFragment : Fragment() {

    private var _binding: FragmentMymenuBinding? = null
    private val binding get() = _binding!!

    private val settingsDataStore by lazy {
        (requireActivity().application as MingleDayApplication).settingsDataStore
    }

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMymenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLanguage()
    }

    private fun setupLanguage() {
        binding.koreanButton.setOnClickListener {
            binding.koreanButton.isSelected = true
            binding.englishButton.isSelected = false
        }

        binding.englishButton.setOnClickListener {
            binding.koreanButton.isSelected = false
            binding.englishButton.isSelected = true
        }

        binding.languageToggleLabel.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            val language = when (checkedId) {
                R.id.korean_button -> "ko"
                R.id.english_button -> "en"
                else -> return@addOnButtonCheckedListener
            }

            val app = requireActivity().application as MingleDayApplication

            viewLifecycleOwner.lifecycleScope.launch {
                app.settingsDataStore.saveLanguage(language)
            }
        }
    }
}