package com.example.budgettracking.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.budgettracking.managers.ProximitySensorManager

open class BaseActivity : AppCompatActivity() {

    private lateinit var proximitySensorManager: ProximitySensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        proximitySensorManager = ProximitySensorManager(this)
    }

    override fun onResume() {
        super.onResume()
        proximitySensorManager.registerListener()
    }

    override fun onPause() {
        super.onPause()
        proximitySensorManager.unregisterListener()
    }
}
