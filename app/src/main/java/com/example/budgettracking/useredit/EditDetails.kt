package com.example.budgettracking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.budgettracking.databinding.ActivityEditDetailsBinding
import com.example.budgettracking.ui.activity.BaseActivity
import com.example.budgettracking.ui.activity.ProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditDetails : BaseActivity() {

    lateinit var viewBinding : ActivityEditDetailsBinding
    lateinit var databaseReference: DatabaseReference
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityEditDetailsBinding.inflate(layoutInflater)
        val view = viewBinding.root

        setContentView(view)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        viewBinding.saveBtn.setOnClickListener {
            val newFirstName = viewBinding.editFirstName.text.toString()
            val newLastName = viewBinding.editLastName.text.toString()
            val userId = firebaseAuth.currentUser?.uid

            if (newFirstName.isNotEmpty() && newLastName.isNotEmpty()) {
                if (userId != null) {
                    val userMap = mapOf(
                        "firstName" to newFirstName,
                        "lastName" to newLastName
                    )
                    databaseReference.child(userId).updateChildren(userMap).addOnSuccessListener {
                        Toast.makeText(this, "Name Updated", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ProfileActivity::class.java)
                        startActivity(intent)
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to update name: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
