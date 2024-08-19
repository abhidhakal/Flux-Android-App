package com.example.budgettracking.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.budgettracking.ui.fragments.ExpenseFragment
import com.example.budgettracking.ui.fragments.IncomeFragment

class AddExpenseAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> IncomeFragment()
            else -> ExpenseFragment()
        }
    }
}
