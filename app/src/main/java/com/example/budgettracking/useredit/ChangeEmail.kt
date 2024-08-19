package com.example.budgettracking.useredit

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.budgettracking.R
import com.example.budgettracking.ui.activity.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.database.FirebaseDatabase

class ChangeEmail : BaseActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG = "ChangeEmail"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_email)

        auth = FirebaseAuth.getInstance()

        val newEmailInput = findViewById<EditText>(R.id.newEmailInput)
        val currentPasswordInput = findViewById<EditText>(R.id.currentPasswordInput)
        val confirmEmailChangeButton = findViewById<Button>(R.id.confirmEmailChangeButton)

        confirmEmailChangeButton.setOnClickListener {
            val newEmail = newEmailInput.text.toString().trim()
            val currentPassword = currentPasswordInput.text.toString().trim()

            if (newEmail.isNotEmpty() && currentPassword.isNotEmpty()) {
                reauthenticateAndChangeEmail(newEmail, currentPassword)
            } else {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun reauthenticateAndChangeEmail(newEmail: String, currentPassword: String) {
        val user = auth.currentUser

        if (user != null && user.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User re-authenticated.")
                        updateEmail(newEmail)
                    } else {
                        Log.w(TAG, "Re-authentication failed: ${task.exception?.message}")
                        Toast.makeText(this, "Re-authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Log.w(TAG, "User is null or email is null.")
            Toast.makeText(this, "User is not authenticated.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateEmail(newEmail: String) {
        val user = auth.currentUser
        val userId = user?.uid

        if (user != null && userId != null) {
            user.updateEmail(newEmail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
                        userRef.child("email").setValue(newEmail)
                            .addOnCompleteListener { task2 ->
                                if (task2.isSuccessful) {
                                    Toast.makeText(this, "Email updated successfully", Toast.LENGTH_SHORT).show()
                                    finish()  // Close the activity after successful update
                                } else {
                                    Log.w(TAG, "Failed to update email in database: ${task2.exception?.message}")
                                    Toast.makeText(this, "Failed to update email in database: ${task2.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Log.w(TAG, "Failed to update email: ${task.exception?.message}")
                        Toast.makeText(this, "Failed to update email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
