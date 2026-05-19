package returns.mingleday.app.ui.main.mymenu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
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
import returns.mingleday.app.ui.main.MainActivity
import returns.mingleday.databinding.FragmentMymenuBinding
import returns.mingleday.app.util.DateFormatter.formatCustom
import returns.mingleday.app.util.FileUtil
import java.time.LocalDateTime

class MymenuFragment : Fragment() {

    private var _binding: FragmentMymenuBinding? = null
    private val binding get() = _binding!!
    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMymenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).setToolbarTitle(R.string.my_info_label)
        setupFetchUserInfo()
        setupLogout()
        setupWithdraw()
        setupUpdateProfileImage()
    }

    private fun setupUpdateProfileImage() {
        binding.profileImage.setOnClickListener {
            galleryLauncher.launch("image/*")
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri ?: return@registerForActivityResult
        val file = FileUtil.uriToFile(
            requireContext(),
            uri,
            "profile.jpg"
        )
        viewLifecycleOwner.lifecycleScope.launch {
            userRepository.updateMyProfileImage(file)
                .onSuccess { imageUrl ->
                    Log.d("MymenuFragment", "프로필 이미지 변경 성공 - $imageUrl")
                    Toast.makeText(requireContext(), "프로필 이미지 변경에 성공했습니다.", Toast.LENGTH_LONG).show()

                    Glide.with(binding.root)
                        .load(imageUrl)
                        .placeholder(R.drawable.default_profile)
                        .fallback(R.drawable.default_profile)
                        .error(R.drawable.default_profile)
                        .into(binding.profileImage)
                }
                .onError {
                    Log.d("MymenuFragment", "프로필 이미지 변경 실패: $it")
                    Toast.makeText(requireContext(), R.string.internal_server_error, Toast.LENGTH_LONG).show()
                }
                .onException {
                    Log.e("MymenuFragment", "프로필 이미지 변경 도중 예외 발생:, $it")
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun setupWithdraw() {
        binding.withdrawButton.setOnClickListener {
            binding.withdrawButton.isEnabled = false
            binding.withdrawButton.setText(R.string.requesting)

            viewLifecycleOwner.lifecycleScope.launch {
                authRepository.withdraw()
                    .onSuccess {
                        Log.d("MymenuFragment", "회원탈퇴 성공")

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
                .onSuccess { response ->
                    Log.d("MymenuFragment", "마이페이지 정보 요청 성공")

                    val dt = LocalDateTime.parse(response.createdAt)
                    binding.realnameValue.text = response.name
                    binding.nicknameValue.text = response.nickname
                    binding.registerDateValue.text = dt.formatCustom(2)
                    binding.belongMingleNumberValue.text = response.belongMingleCnt.toString()
                    binding.emailValue.text = response.email

                    Glide.with(binding.root)
                        .load(response.profileUrl)
                        .placeholder(R.drawable.default_profile)
                        .fallback(R.drawable.default_profile)
                        .error(R.drawable.default_profile)
                        .into(binding.profileImage)
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
}