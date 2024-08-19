package com.example.budgettracking.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.budgettracking.databinding.ActivitySettingsBinding
import com.example.budgettracking.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class  SignUpActivity : BaseActivity() {
    lateinit var viewBindingSetting: ActivitySettingsBinding
    lateinit var viewBinding: ActivitySignUpBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var databaseReference: DatabaseReference
    var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var sharedPreferences = getSharedPreferences("Mode", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val nightMode = sharedPreferences.getBoolean("night", false)

        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        viewBinding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = viewBinding.root

        setContentView(view)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        viewBinding.registerBtn.setOnClickListener {
            val fn = viewBinding.fnInput.text.toString()
            val ln = viewBinding.lnInput.text.toString()
            val email = viewBinding.mailInput.text.toString()
            val pw = viewBinding.passwordField.text.toString()
            val confirmPw = viewBinding.confirmPasswordField.text.toString()

            if (fn.isNotEmpty() && ln.isNotEmpty() && email.isNotEmpty() && pw.isNotEmpty() && confirmPw.isNotEmpty()) {
                if (pw == confirmPw) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = firebaseAuth.currentUser?.uid
                            val userMap = mapOf(
                                "firstName" to fn,
                                "lastName" to ln,
                                "email" to email
                            )

                            if (userId != null) {
                                databaseReference.child(userId).setValue(userMap).addOnSuccessListener {
                                    Toast.makeText(this, "Account Registered", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, DashboardActivity::class.java)
                                    startActivity(intent)
                                }.addOnFailureListener { e ->
                                    Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, task.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        viewBinding.goLogin.setOnClickListener {
            val intent = Intent(this@SignUpActivity, LogInActivity::class.java)
            startActivity(intent)
        }
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
