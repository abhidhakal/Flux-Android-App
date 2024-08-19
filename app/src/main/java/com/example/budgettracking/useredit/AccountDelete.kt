package com.example.budgettracking.useredit

import android.os.Bundle
import android.text.TextUtils
import com.example.budgettracking.databinding.ActivityAccountDeleteBinding
import com.example.budgettracking.ui.activity.BaseActivity
import com.example.budgettracking.utils.AccountUtils

class AccountDelete : BaseActivity() {
    lateinit var viewBinding : ActivityAccountDeleteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityAccountDeleteBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.deleteAccountButton.setOnClickListener {
            val currentPassword = viewBinding.currentPassword.text.toString().trim()
            val confirmPassword = viewBinding.confirmPassword.text.toString().trim()

            if (TextUtils.isEmpty(currentPassword)) {
                viewBinding.currentPassword.error = "Current password is required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(confirmPassword)) {
                viewBinding.confirmPassword.error = "Confirm password is required"
                return@setOnClickListener
            }

            if (currentPassword != confirmPassword) {
                viewBinding.confirmPassword.error = "Passwords do not match"
                return@setOnClickListener
            }

            AccountUtils.confirmAndDeleteAccount(this, currentPassword)
        }

    }
}