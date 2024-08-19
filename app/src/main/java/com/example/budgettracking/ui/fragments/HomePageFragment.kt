package com.example.budgettracking.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgettracking.R
import com.example.budgettracking.adapter.ExpenseAdapter
import com.example.budgettracking.databinding.FragmentHomePageBinding
import com.example.budgettracking.models.Expense
import com.example.budgettracking.ui.activity.EditExpenseActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class HomePageFragment : Fragment(), ExpenseAdapter.OnItemClickListener {

    private lateinit var binding: FragmentHomePageBinding
    private lateinit var expenseAdapter: ExpenseAdapter
    private val expenseList = mutableListOf<Expense>()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private var initialBalance: Double = 0.0
    private var totalExpenses: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        expenseAdapter = ExpenseAdapter(expenseList, this)
        binding.expenseRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = expenseAdapter
        }

        loadExpenses()
        fetchUserData()
        fetchInitialBalance()

        binding.clearExpensesButton.setOnClickListener {
            clearAllExpenses()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadExpenses() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("expenses")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (isAdded) {
                    expenseList.clear()
                    totalExpenses = 0.0
                    for (document in querySnapshot.documents) {
                        val expense = document.toObject(Expense::class.java)
                        expense?.id = document.id // Set the document ID
                        expense?.let {
                            expenseList.add(it)
                            totalExpenses += it.expenseAmount ?: 0.0
                        }
                    }
                    expenseAdapter.notifyDataSetChanged()
                    updateRemainingBalance()
                }
            }
            .addOnFailureListener { exception ->
                if (isAdded) {
                    Toast.makeText(requireContext(), "Failed to load expenses/ Expenses not added", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun fetchUserData() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            databaseReference.child("users").child(userId).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (isAdded) {
                        if (snapshot.exists()) {
                            val firstName = snapshot.child("firstName").getValue(String::class.java)
                            val lastName = snapshot.child("lastName").getValue(String::class.java)

                            binding.firstname.text = firstName ?: "First Name"
                            binding.lastname.text = lastName ?: "Last Name"
                        } else {
                            Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    private fun fetchInitialBalance() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            databaseReference.child("users").child(userId).child("initialBalance")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (isAdded) {
                            if (snapshot.exists()) {
                                initialBalance = snapshot.getValue(Double::class.java) ?: 0.0
                                binding.initbalance.text = getString(R.string.npr_format, initialBalance)
                                updateRemainingBalance()
                            } else {
                                Toast.makeText(requireContext(), "Initial balance not found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        if (isAdded) {
                            Toast.makeText(requireContext(), "Failed to fetch initial balance/ Balance not added", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }
    }

    private fun updateRemainingBalance() {
        if (!isAdded) return
        val remainingBalance = initialBalance - totalExpenses
        binding.remainingbalance.text = getString(R.string.npr_format, remainingBalance)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun clearAllExpenses() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("expenses")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (isAdded) {
                    for (document in querySnapshot.documents) {
                        db.collection("expenses").document(document.id).delete()
                    }
                    expenseList.clear()
                    expenseAdapter.notifyDataSetChanged()
                    totalExpenses = 0.0
                    updateRemainingBalance()
                    Toast.makeText(requireContext(), "All expenses cleared", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                if (isAdded) {
                    Toast.makeText(requireContext(), "Failed to clear expenses", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onEditClick(expense: Expense) {
        val intent = Intent(requireContext(), EditExpenseActivity::class.java)
        intent.putExtra("expense_id", expense.id)
        startActivity(intent)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDeleteClick(expense: Expense) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("expenses")
            .document(expense.id!!)
            .delete()
            .addOnSuccessListener {
                if (isAdded) {
                    expenseList.remove(expense)
                    expenseAdapter.notifyDataSetChanged()
                    totalExpenses -= expense.expenseAmount ?: 0.0
                    updateRemainingBalance()
                    Toast.makeText(requireContext(), "Expense deleted", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                if (isAdded) {
                    Toast.makeText(requireContext(), "Failed to delete expense", Toast.LENGTH_SHORT).show()
                }
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshData() {
        // Reload your data here, update the list or reload from database
        // Clear existing data
        expenseList.clear()
        totalExpenses = 0.0
        expenseAdapter.notifyDataSetChanged() // Notify adapter to refresh

        // Reload data
        loadExpenses()
        fetchUserData()
        fetchInitialBalance()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
