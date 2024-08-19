package com.example.budgettracking.ui.fragments

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.example.budgettracking.R
import com.example.budgettracking.databinding.FragmentIncomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class IncomeFragment : Fragment() {
    private lateinit var binding: FragmentIncomeBinding
    private lateinit var auth: FirebaseAuth

    private var CHANNEL_ID = "1"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIncomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.setInitialBalanceButton.setOnClickListener {
            setInitialBalance()
            if (areNotificationsEnabled()) {
                showNotification()
            }
        }

        binding.setAddBalanceButton.setOnClickListener {
            addBalance()
        }

        binding.clearBtn1.setOnClickListener {
            binding.initialBalanceInput.text.clear()
        }

        binding.clearBtn2.setOnClickListener {
            binding.addBalanceInput.text.clear()
        }
    }

    private fun setInitialBalance() {
        if (!isAdded) return

        val initialBalance = binding.initialBalanceInput.text.toString().toDoubleOrNull()

        if (initialBalance == null) {
            Toast.makeText(
                requireContext(),
                "Please enter a valid initial balance",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val userId = auth.currentUser?.uid ?: return

        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)

        userRef.child("initialBalance").setValue(initialBalance)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Initial balance set successfully",
                    Toast.LENGTH_SHORT
                ).show()
                binding.initialBalanceInput.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Error setting initial balance: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun addBalance() {
        if (!isAdded) return

        val addBalance = binding.addBalanceInput.text.toString().toDoubleOrNull()

        if (addBalance == null) {
            Toast.makeText(requireContext(), "Please enter a valid balance", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val userId = auth.currentUser?.uid ?: return

        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)

        userRef.child("initialBalance").get().addOnSuccessListener { snapshot ->
            val currentBalance = snapshot.getValue(Double::class.java) ?: 0.0
            val newBalance = currentBalance + addBalance

            userRef.child("initialBalance").setValue(newBalance)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Balance added successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.addBalanceInput.text.clear()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Error adding balance: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun showNotification() {
        val CHANNEL_ID = "MY_CHANNEL_ID"
        val notificationId = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Channel"
            val descriptionText = "Channel for balance notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.mipmap.logo_final)
            .setContentTitle("Balance")
            .setContentText("Added Initial Balance")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request permission if not granted
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
                return
            }
            notify(notificationId, builder.build())
        }
    }

    private fun areNotificationsEnabled(): Boolean {
        val sharedPreferences = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("notifications", true)
    }
}
