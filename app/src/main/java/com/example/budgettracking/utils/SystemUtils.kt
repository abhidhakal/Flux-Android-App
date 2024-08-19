package com.example.budgettracking.utils

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import android.graphics.Color
import android.widget.TextView
import com.example.budgettracking.ui.activity.LogInActivity
import com.example.budgettracking.R
import com.google.firebase.auth.FirebaseAuth

object SystemUtils {
    fun logOut(context: Context){
        val alertDialogBuilder = AlertDialog.Builder(context)

        alertDialogBuilder.setTitle("Logout")
        alertDialogBuilder.setMessage("Do you want to log out?")

        alertDialogBuilder.setPositiveButton("Yes"){_,_->
            val mAuth = FirebaseAuth.getInstance()
            mAuth.signOut()
            context.startActivity(Intent(context, LogInActivity::class.java))
            if (context is AppCompatActivity) {
                context.finish()
            }
        }

        alertDialogBuilder.setNegativeButton("No",null)
        val alertDialog = alertDialogBuilder.create()

        alertDialog.setOnShowListener {

            val titleView = alertDialog.findViewById<TextView>(android.R.id.title)
            titleView?.setTextColor(Color.BLACK)

            val messageView = alertDialog.findViewById<TextView>(android.R.id.message)
            val isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
            messageView?.setTextColor(if (isDarkMode) Color.WHITE else Color.BLACK)

            alertDialog.window?.setBackgroundDrawableResource(if (isDarkMode) R.color.black else R.color.white)
        }

        alertDialog.show()
    }
}
