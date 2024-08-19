package com.example.budgettracking.useredit

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.budgettracking.ui.activity.ProfileActivity
import com.example.budgettracking.databinding.ActivityChangePasswordBinding
import com.example.budgettracking.ui.activity.BaseActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePassword : BaseActivity() {
    lateinit var viewBinding : ActivityChangePasswordBinding
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        viewBinding.changePasswordButton.setOnClickListener {
            val currentPassword = viewBinding.currentPassword.text.toString().trim()
            val newPassword = viewBinding.newPassword.text.toString().trim()
            val confirmNewPassword = viewBinding.confirmNewPassword.text.toString().trim()

            if (TextUtils.isEmpty(currentPassword)) {
                viewBinding.currentPassword.error = "Current password is required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(newPassword)) {
                viewBinding.newPassword.error = "New password is required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(confirmNewPassword)) {
                viewBinding.confirmNewPassword.error = "Confirm new password is required"
                return@setOnClickListener
            }

            if (newPassword != confirmNewPassword) {
                viewBinding.confirmNewPassword.error = "Passwords do not match"
                return@setOnClickListener
            }

            changePassword(currentPassword, newPassword)

            startActivity(Intent(this, ProfileActivity::class.java))

        }
    }
    private fun changePassword(currentPassword: String, newPassword: String) {
        val user = firebaseAuth.currentUser
        if (user != null && user.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

            user.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to change password: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Re-authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}