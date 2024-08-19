package com.example.budgettracking.utils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.budgettracking.ui.activity.LogInActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object AccountUtils {

    fun confirmAndDeleteAccount(context: Context, currentPassword: String) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser

        if (user != null && user.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

            user.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Show confirmation dialog
                    AlertDialog.Builder(context)
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete your account?")
                        .setPositiveButton("Yes") { _, _ ->
                            deleteAccount(context, user.uid)
                        }
                        .setNegativeButton("No", null)
                        .show()
                } else {
                    Toast.makeText(context, "Re-authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteAccount(context: Context, userId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId)
        val firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser

        databaseReference.removeValue().addOnCompleteListener { dbTask ->
            if (dbTask.isSuccessful) {
                user?.delete()?.addOnCompleteListener { deleteTask ->
                    if (deleteTask.isSuccessful) {
                        Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, LogInActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "Failed to delete account: ${deleteTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Failed to delete user data: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}