package returns.mingleday.app.ui.mymenu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import returns.mingleday.R
import returns.mingleday.app.MingleDayApplication
import returns.mingleday.app.data.local.TokenProvider
import returns.mingleday.app.data.remote.network.onError
import returns.mingleday.app.data.remote.network.onException
import returns.mingleday.app.data.remote.network.onSuccess
import returns.mingleday.app.data.repository.AuthRepository
import returns.mingleday.app.data.repository.UserRepository
import returns.mingleday.app.ui.auth.LoginActivity
import returns.mingleday.databinding.FragmentMymenuBinding
import returns.mingleday.util.DateFormatter.formatCustom
import java.time.LocalDateTime


class MymenuFragment : Fragment() {

    private var _binding: FragmentMymenuBinding? = null
    private val binding get() = _binding!!
    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()

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
        setupFetchUserInfo()
        setupLogout()
    }

    private fun setupLogout() {
        binding.logoutButton.setOnClickListener {
            binding.logoutButton.isEnabled = false
            binding.logoutButton.text = R.string.requesting.toString()

            viewLifecycleOwner.lifecycleScope.launch {
                authRepository.logout()
                    .onSuccess {
                        Log.d("MymenuFragment", "마이페이지 정보 요청 성공")

                        TokenProvider.clear()
                        val app = requireActivity().application as MingleDayApplication
                        app.tokenDataStore.clearTokens()

                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    .onError {
                        Log.d("MymenuFragment", "로그아웃 실패: $it")
                        Toast.makeText(requireContext(), R.string.internal_server_error, Toast.LENGTH_LONG).show()
                        binding.logoutButton.isEnabled = true
                        binding.logoutButton.text = R.string.logout_button.toString()
                    }
                    .onException {
                        Log.e("MymenuFragment", "로그아웃 도중 예외 발생:, $it")
                        Toast.makeText(requireContext(), R.string.internal_server_error, Toast.LENGTH_LONG).show()
                        binding.logoutButton.isEnabled = true
                        binding.logoutButton.text = R.string.logout_button.toString()
                    }
            }
        }
    }

    private fun setupFetchUserInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            userRepository.getMyPageInfo()
                .onSuccess { myPageUserResponse ->
                    Log.d("MymenuFragment", "마이페이지 정보 요청 성공")

                    val dt = LocalDateTime.parse(myPageUserResponse.createdAt)
                    binding.realnameValue.text = myPageUserResponse.name
                    binding.nicknameValue.text = myPageUserResponse.nickname
                    binding.registerDateValue.text = dt.formatCustom(2)
                    binding.belongMingleNumberValue.text = myPageUserResponse.belongMingleCnt.toString()
                    binding.emailValue.text = myPageUserResponse.email
                }
                .onError {
                    Log.d("MymenuFragment", "마이페이지 정보 요청 실패: $it")
                    Toast.makeText(requireContext(), R.string.internal_server_error, Toast.LENGTH_LONG).show()
                }
                .onException {
                    Log.e("MymenuFragment", "마이페이지 정보 요청 도중 예외 발생:, $it")
                    Toast.makeText(requireContext(), R.string.internal_server_error, Toast.LENGTH_LONG).show()
                }
        }
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