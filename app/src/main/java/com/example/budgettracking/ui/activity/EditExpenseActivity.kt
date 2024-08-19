package com.example.budgettracking.ui.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.budgettracking.R
import com.example.budgettracking.databinding.ActivityEditExpenseBinding
import com.example.budgettracking.databinding.ActivitySettingsBinding
import com.example.budgettracking.models.Expense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditExpenseActivity : BaseActivity() {
    private lateinit var binding: ActivityEditExpenseBinding
    private lateinit var db: FirebaseFirestore
    private var expenseId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var sharedPreferences = getSharedPreferences("Mode", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val nightMode = sharedPreferences.getBoolean("night", false)

        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        binding = ActivityEditExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        expenseId = intent.getStringExtra("expense_id")

        if (expenseId == null) {
            Toast.makeText(this, "Invalid expense ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadExpenseDetails()

        binding.saveButton.setOnClickListener {
            saveExpenseDetails()
        }
    }

    private fun loadExpenseDetails() {
        expenseId?.let {
            db.collection("expenses").document(it).get()
                .addOnSuccessListener { documentSnapshot ->
                    val expense = documentSnapshot.toObject(Expense::class.java)
                    expense?.let {
                        binding.amountEditText.setText(it.expenseAmount.toString())
                        binding.descriptionEditText.setText(it.expenseDescription)
                        binding.categoryEditText.setText(it.category)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load expense details", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveExpenseDetails() {
        val updatedExpense = Expense(
            id = expenseId,
            expenseAmount = binding.amountEditText.text.toString().toDoubleOrNull(),
            expenseDescription = binding.descriptionEditText.text.toString(),
            category = binding.categoryEditText.text.toString(),
            userId = FirebaseAuth.getInstance().currentUser?.uid
        )

        expenseId?.let {
            db.collection("expenses").document(it).set(updatedExpense)
                .addOnSuccessListener {
                    Toast.makeText(this, "Expense updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update expense", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
