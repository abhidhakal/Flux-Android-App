package com.example.budgettracking.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.budgettracking.R
import com.example.budgettracking.databinding.ActivityAddExpenseBinding
import com.example.budgettracking.models.Expense
import com.example.budgettracking.ui.fragments.ExpenseFragment
import com.example.budgettracking.ui.fragments.IncomeFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AddExpenseActivity : BaseActivity() {

    lateinit var viewBinding : ActivityAddExpenseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        viewPager.adapter = ViewPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Add Balance"
                1 -> tab.text = "Add Expense"
            }
        }.attach()

        saveOrUpdateExpense()

        setResult(Activity.RESULT_OK)
    }

    private inner class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> IncomeFragment()
                1 -> ExpenseFragment()
                else -> throw IllegalStateException("Unexpected position $position")
            }
        }
    }

    private fun saveOrUpdateExpense() {
        // Replace with your logic to save or update expense
        // For demonstration, just showing how to pass data back
        val expense = Expense("Sample Expense", 50.0.toString())
        // Example: Pass back the expense ID if necessary
        val intent = Intent()
        intent.putExtra("expense_id", expense.id)
        setResult(Activity.RESULT_OK, intent)
    }
}
