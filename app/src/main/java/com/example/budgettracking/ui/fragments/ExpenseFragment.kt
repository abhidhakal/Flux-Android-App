package com.example.budgettracking.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.budgettracking.R
import com.example.budgettracking.databinding.FragmentExpenseBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ExpenseFragment : Fragment() {
    lateinit var binding : FragmentExpenseBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupCategorySpinner()

        binding.submitButton.setOnClickListener {
            addExpense()
        }

        binding.clearBtn.setOnClickListener {
            binding.expenseAmountInput.text.clear()
            binding.expenseDescriptionInput.text.clear()
        }

    }

    private fun setupCategorySpinner() {
        if (!isAdded) return

        val categories = listOf("Food", "Transport", "Entertainment", "Others") // Example categories
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = adapter
    }

    private fun addExpense() {
        if (!isAdded) return

        val expenseAmount = binding.expenseAmountInput.text.toString().toDoubleOrNull()
        val expenseDescription = binding.expenseDescriptionInput.text.toString()
        val category = binding.categorySpinner.selectedItem.toString()

        if (expenseAmount == null || expenseDescription.isBlank() || category.isBlank()) {
            Toast.makeText(requireContext(), "Please enter valid data", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid ?: return

        val expense = hashMapOf(
            "userId" to userId,
            "expenseAmount" to expenseAmount,
            "expenseDescription" to expenseDescription,
            "category" to category,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("expenses")
            .add(expense)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Expense added successfully", Toast.LENGTH_SHORT).show()
                binding.expenseAmountInput.text.clear()
                binding.expenseDescriptionInput.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error adding expense: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}