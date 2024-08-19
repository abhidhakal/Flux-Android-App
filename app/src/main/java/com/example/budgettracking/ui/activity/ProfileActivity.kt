package com.example.budgettracking.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.budgettracking.EditDetails
import com.example.budgettracking.databinding.ActivityProfileBinding
import com.example.budgettracking.useredit.AccountDelete
import com.example.budgettracking.useredit.ChangeEmail
import com.example.budgettracking.useredit.ChangePassword
import com.example.budgettracking.utils.SystemUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : BaseActivity() {
    private lateinit var viewBinding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        var sharedPreferences = getSharedPreferences("Mode", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val nightMode = sharedPreferences.getBoolean("night", false)

        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        fetchUserData()

        viewBinding.editDetailsButton.setOnClickListener {
            startActivity(Intent(this, EditDetails::class.java))
        }

        viewBinding.editEmailButton.setOnClickListener {
            startActivity(Intent(this, ChangeEmail::class.java))
        }

        viewBinding.logoutBtn.setOnClickListener {
            SystemUtils.logOut(this)
        }

        viewBinding.ChangePasswordButton.setOnClickListener {
            startActivity(Intent(this, ChangePassword::class.java))
        }

        viewBinding.deleteAccButton.setOnClickListener {
            startActivity(Intent(this, AccountDelete::class.java))
        }
    }

    private fun fetchUserData() {
        val userId = firebaseAuth.currentUser?.uid

        if (userId != null) {
            databaseReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val firstName = snapshot.child("firstName").getValue(String::class.java)
                        val lastName = snapshot.child("lastName").getValue(String::class.java)

                        viewBinding.firstname.text = firstName
                        viewBinding.lastname.text = lastName
                    } else {
                        Toast.makeText(this@ProfileActivity, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ProfileActivity, "Failed to fetch user data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
