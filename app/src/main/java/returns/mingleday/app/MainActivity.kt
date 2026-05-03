package returns.mingleday.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import returns.mingleday.R
import returns.mingleday.app.ui.mymenu.MingleListFragment
import returns.mingleday.app.ui.mymenu.MymenuFragment
import returns.mingleday.app.ui.mymenu.ScheduleFragment
import returns.mingleday.app.ui.mymenu.SearchFragment
import returns.mingleday.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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