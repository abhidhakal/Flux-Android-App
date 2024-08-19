package com.example.budgettracking.ui.activity


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.budgettracking.R
import com.example.budgettracking.databinding.ActivityDashboardBinding
import com.example.budgettracking.fragments.HomePageFragment
import com.example.budgettracking.utils.SystemUtils

class DashboardActivity : BaseActivity() {

    private lateinit var viewBinding: ActivityDashboardBinding
    private var backPressedTime: Long = 0
    private val ADD_EXPENSE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val sharedPreferences = getSharedPreferences("Mode", MODE_PRIVATE)
        val nightMode = sharedPreferences.getBoolean("night", false)
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        setDefaultFragment(HomePageFragment())

        viewBinding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> startActivity(Intent(this@DashboardActivity, ProfileActivity::class.java))
                R.id.nav_settings -> startActivity(Intent(this@DashboardActivity, SettingsActivity::class.java))
                R.id.nav_logout -> SystemUtils.logOut(this)
                R.id.abt_us -> startActivity(Intent(this@DashboardActivity, AboutUsActivity::class.java))
                R.id.feedback -> startActivity(Intent(this@DashboardActivity, FeedbackActivity::class.java))
            }
            true
        }

        // Floating action button click listener
        viewBinding.fabAddExpense.setOnClickListener {
            startAddExpenseActivityForResult()
        }
    }

    private fun setDefaultFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, fragment)
            commit()
        }
    }

    private fun startAddExpenseActivityForResult() {
        val intent = Intent(this, AddExpenseActivity::class.java)
        startActivityForResult(intent, ADD_EXPENSE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_EXPENSE_REQUEST && resultCode == Activity.RESULT_OK) {
            // Refresh your fragment or perform any update
            val fragment = supportFragmentManager.findFragmentById(R.id.frameLayout)
            if (fragment is HomePageFragment) {
                fragment.refreshData()
            }
        }
    }

    fun openDrawer(view: android.view.View) {
        val drawerLayout = viewBinding.main
        drawerLayout.openDrawer(findViewById(R.id.navigation_view))
    }

    override fun onBackPressed() {
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finishAffinity()
        } else {
            Toast.makeText(this, "Press back again to leave the app.", Toast.LENGTH_LONG).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}
