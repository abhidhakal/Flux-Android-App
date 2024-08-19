package com.example.budgettracking.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.budgettracking.databinding.ActivityFeedbackBinding
import com.example.budgettracking.models.Feedback
import com.google.firebase.firestore.FirebaseFirestore

class FeedbackActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityFeedbackBinding
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val sharedPreferences = getSharedPreferences("Mode", MODE_PRIVATE)
        val nightMode = sharedPreferences.getBoolean("night", false)
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        firestore = FirebaseFirestore.getInstance()

        viewBinding.submitFeedbackBtn.setOnClickListener {
            val title = viewBinding.feedbackTitle.text.toString()
            val email = viewBinding.feedbackEmail.text.toString()
            val description = viewBinding.feedbackDescription.text.toString()

            if (title.isNotEmpty() && email.isNotEmpty() && description.isNotEmpty()) {
                val feedback = Feedback(title, email, description)
                firestore.collection("feedback")
                    .add(feedback)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show()
                        // Clear all EditText fields
                        viewBinding.feedbackTitle.text.clear()
                        viewBinding.feedbackEmail.text.clear()
                        viewBinding.feedbackDescription.text.clear()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error submitting feedback", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
