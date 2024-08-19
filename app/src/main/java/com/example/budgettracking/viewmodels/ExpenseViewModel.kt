package com.example.budgettracking.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    fun submitExpense(initialBalance: Double, addBalance: Double, expenseAmount: Double, expenseDescription: String) {
        val totalBalance = initialBalance + addBalance - expenseAmount

        user?.let {
            val expenseData = hashMapOf(
                "userId" to user.uid,
                "initialBalance" to initialBalance,
                "addBalance" to addBalance,
                "expenseAmount" to expenseAmount,
                "expenseDescription" to expenseDescription,
                "totalBalance" to totalBalance
            )

            db.collection("expenses")
                .add(expenseData)
                .addOnSuccessListener {
                    Toast.makeText(getApplication(), "Expense recorded successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(getApplication(), "Error recording expense: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
