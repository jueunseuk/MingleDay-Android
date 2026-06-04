package returns.mingleday.app.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import returns.mingleday.R
import returns.mingleday.app.data.remote.intercepter.SessionManager
import returns.mingleday.app.data.remote.network.onError
import returns.mingleday.app.data.remote.network.onException
import returns.mingleday.app.data.remote.network.onSuccess
import returns.mingleday.app.data.repository.MingleRepository
import returns.mingleday.app.ui.auth.LoginActivity
import returns.mingleday.app.ui.main.mingle.MingleListFragment
import returns.mingleday.app.ui.main.mymenu.MymenuFragment
import returns.mingleday.app.ui.main.schedule.ScheduleAddFragment
import returns.mingleday.app.ui.main.schedule.MonthlyScheduleFragment
import returns.mingleday.app.ui.main.search.SearchFragment
import returns.mingleday.app.ui.main.side.MingleDrawerAdapter
import returns.mingleday.app.ui.main.side.NotificationDrawerAdapter
import returns.mingleday.app.util.ToastUtil
import returns.mingleday.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mingleRepository = MingleRepository()
    private lateinit var mingleDrawerAdapter: MingleDrawerAdapter
    private lateinit var notificationAdapter: NotificationDrawerAdapter

    var mingleId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topBar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            replaceFragment(MonthlyScheduleFragment())
            binding.bottomBar.selectedItemId = R.id.schedule
        }

        setupDrawer()
        setupNotificationDrawer()
        setupBottomNavigation()
        setupSession()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_calendar -> {
                val fragment = ScheduleAddFragment().apply {
                    arguments = Bundle().apply {
                        putInt("mingleId", mingleId)
                    }
                }

                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame, fragment)
                    .addToBackStack(null)
                    .commit()

                true
            }

            R.id.action_alarm -> {
                lifecycleScope.launch {
                    mingleRepository.getMyLogs()
                        .onSuccess { response ->
                            notificationAdapter.submitList(response)

                            binding.drawerLayout.openDrawer(
                                binding.notificationDrawer
                            )
                        }
                        .onError {
                            ToastUtil.makeErrorToast(this@MainActivity)
                            Log.d("MainActivity", "내 밍글 로그 불러오기 중 에러 발생 - $it")
                        }
                        .onException {
                            ToastUtil.makeExceptionToast(this@MainActivity, it)
                            Log.d("MainActivity", "내 밍글 로그 불러오기 중 예외 발생 - $it")
                        }
                }
                true
            }

            else ->
                super.onOptionsItemSelected(item)
        }
    }

    private fun setupNotificationDrawer() {
        notificationAdapter = NotificationDrawerAdapter()

        binding.notificationRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = notificationAdapter
        }
    }

    private fun setupDrawer() {
        mingleDrawerAdapter = MingleDrawerAdapter { mingle ->
            binding.drawerLayout.close()
            val fragment = MonthlyScheduleFragment().apply {
                arguments = Bundle().apply {
                    putInt("mingleId", mingle.mingleId)
                    putString("mingleName", mingle.mingleName)
                }
            }

            replaceFragment(fragment)
        }

        binding.drawerMingleRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.drawerMingleRecyclerView.adapter = mingleDrawerAdapter

        binding.topBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
            fetchDrawerMingles()
        }
    }

    private fun fetchDrawerMingles() {
        lifecycleScope.launch {
            mingleRepository.getMyMingles()
                .onSuccess { response ->
                    mingleDrawerAdapter.submitList(response)
                    Log.d("MainActivity", "밍글 선택 Drawer 불러오기")
                }
                .onError {
                    ToastUtil.makeErrorToast(this@MainActivity)
                    Log.d("MainActivity", "밍글 선택 Drawer 불러오기 중 에러 발생 - $it")
                }
                .onException {
                    ToastUtil.makeExceptionToast(this@MainActivity, it)
                    Log.d("MainActivity", "밍글 선택 Drawer 불러오기 중 예외 발생 - $it")
                }
        }
    }

    fun setToolbarTitle(resId: Int) {
        supportActionBar?.setTitle(resId)
    }

    fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    private fun setupSession() {
        lifecycleScope.launch {
            SessionManager.logoutEvent.collect {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.schedule -> {
                    replaceFragment(MonthlyScheduleFragment())
                    true
                }
                R.id.mingle -> {
                    replaceFragment(MingleListFragment())
                    true
                }
                R.id.search -> {
                    replaceFragment(SearchFragment())
                    true
                }
                R.id.menu -> {
                    replaceFragment(MymenuFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.mainFrame.id, fragment)
            .commit()
    }
}