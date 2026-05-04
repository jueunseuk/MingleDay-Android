package returns.mingleday.app.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import returns.mingleday.R
import returns.mingleday.app.data.remote.intercepter.SessionManager
import returns.mingleday.app.ui.auth.LoginActivity
import returns.mingleday.app.ui.main.mingle.MingleListFragment
import returns.mingleday.app.ui.main.mymenu.MymenuFragment
import returns.mingleday.app.ui.main.schedule.ScheduleFragment
import returns.mingleday.app.ui.main.search.SearchFragment
import returns.mingleday.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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

        setupBottomNavigation()
        setupSession()
    }

    fun setToolbarTitle(resId: Int) {
        supportActionBar?.setTitle(resId)
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