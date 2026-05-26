package returns.mingleday.app.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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
import returns.mingleday.app.ui.main.schedule.ScheduleFragment
import returns.mingleday.app.ui.main.search.SearchFragment
import returns.mingleday.app.ui.main.side.MingleDrawerAdapter
import returns.mingleday.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mingleRepository = MingleRepository()
    private lateinit var mingleDrawerAdapter: MingleDrawerAdapter

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
            replaceFragment(ScheduleFragment())
            binding.bottomBar.selectedItemId = R.id.schedule
        }

        setupDrawer()
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
                // 알림 버튼 클릭
                // 코드 추가
                true
            }

            else ->
                super.onOptionsItemSelected(item)
        }
    }

    private fun setupDrawer() {
        mingleDrawerAdapter = MingleDrawerAdapter { mingle ->
            binding.drawerLayout.close()
            val fragment = ScheduleFragment().apply {
                arguments = Bundle().apply {
                    putInt("mingleId", mingle.mingleId)
                }
            }

            replaceFragment(fragment)
        }

        binding.drawerMingleRecyclerView.layoutManager =
            LinearLayoutManager(this)

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
                }
                .onError {
                    Toast.makeText(
                        this@MainActivity,
                        R.string.internal_server_error,
                        Toast.LENGTH_LONG
                    ).show()
                }
                .onException {
                    Toast.makeText(
                        this@MainActivity,
                        R.string.internal_server_error,
                        Toast.LENGTH_LONG
                    ).show()
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
                    replaceFragment(ScheduleFragment())
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