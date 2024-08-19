package com.example.budgettracking.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationManagerCompat
import com.example.budgettracking.EditDetails
import com.example.budgettracking.databinding.ActivitySettingsBinding
import com.example.budgettracking.useredit.ChangePassword
import com.example.budgettracking.utils.SystemUtils

class SettingsActivity : BaseActivity() {
    private lateinit var viewBinding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        sharedPreferences = getSharedPreferences("Mode", MODE_PRIVATE)
        val nightMode = sharedPreferences.getBoolean("night", false)
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        val notificationsEnabled = sharedPreferences.getBoolean("notifications", true)

        viewBinding.switchNotifications.isChecked = notificationsEnabled
        viewBinding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("notifications", isChecked)
            editor.apply()
            if (isChecked) {
                // Enable notifications
                enableNotifications()
            } else {
                // Disable notifications
                disableNotifications()
            }
        }

        viewBinding.editprofilebtn.setOnClickListener {
            startActivity(Intent(this, EditDetails::class.java))
        }

        viewBinding.changepwbutton.setOnClickListener {
            startActivity(Intent(this, ChangePassword::class.java))
        }

        viewBinding.logoutBtn.setOnClickListener {
            SystemUtils.logOut(this)
        }

        if (notificationsEnabled) {
            enableNotifications()
        } else {
            disableNotifications()
        }
    }

    private fun enableNotifications() {
        NotificationManagerCompat.from(applicationContext).cancelAll()
    }

    private fun disableNotifications() {
        NotificationManagerCompat.from(applicationContext).cancelAll()
    }
}
