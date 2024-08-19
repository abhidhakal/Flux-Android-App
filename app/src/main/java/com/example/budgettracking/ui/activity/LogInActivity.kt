package com.example.budgettracking.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.budgettracking.R
import com.example.budgettracking.databinding.ActivityLogInBinding
import com.example.budgettracking.managers.BiometricPromptManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LogInActivity : BaseActivity() {

    private lateinit var viewBinding: ActivityLogInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val biometricPromptManager by lazy { BiometricPromptManager(this) }
    private var backPressedTime: Long = 0

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("Mode", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val nightMode = sharedPreferences.getBoolean("night", false)

        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        viewBinding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        setupBiometricAuthentication()

        viewBinding.loginBtn.setOnClickListener {
            val email = viewBinding.mailInput.text.toString()
            val pw = viewBinding.passwordField.text.toString()

            if (email.isNotEmpty() && pw.isNotEmpty()) {
                // Attempt email/password login
                firebaseAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Store the credentials securely
                        storeUserCredentials(email, pw)
                        Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, DashboardActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Fill all empty fields", Toast.LENGTH_SHORT).show()
            }
        }

        viewBinding.goSignup.setOnClickListener {
            val intent = Intent(this@LogInActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        viewBinding.biometricLoginBtn.setOnClickListener {
            biometricPromptManager.showBiometricPrompt(
                title = "Biometric Login",
                description = "Use your fingerprint to login"
            )
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupBiometricAuthentication() {
        coroutineScope.launch {
            biometricPromptManager.promptResults.collect { result ->
                when (result) {
                    is BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
                        // Retrieve and use stored credentials for login
                        loginWithStoredCredentials()
                    }
                    is BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
                        Toast.makeText(this@LogInActivity, "Biometric authentication failed", Toast.LENGTH_SHORT).show()
                    }
                    is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                        Toast.makeText(this@LogInActivity, "Biometric authentication error: ${result.error}", Toast.LENGTH_SHORT).show()
                    }
                    is BiometricPromptManager.BiometricResult.AuthenticationNotSet -> {
                        Toast.makeText(this@LogInActivity, "Biometric authentication not set", Toast.LENGTH_SHORT).show()
                    }
                    BiometricPromptManager.BiometricResult.HardwareUnavailable -> {
                        Toast.makeText(this@LogInActivity, "Biometric hardware unavailable", Toast.LENGTH_SHORT).show()
                    }
                    BiometricPromptManager.BiometricResult.FeatureUnavailable -> {
                        Toast.makeText(this@LogInActivity, "Biometric feature unavailable", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun storeUserCredentials(email: String, password: String) {
        val masterKey = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            this,
            "user_credentials",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        sharedPreferences.edit {
            putString("email", email)
            putString("password", password)
        }
    }

    private fun loginWithStoredCredentials() {
        val masterKey = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            this,
            "user_credentials",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val email = sharedPreferences.getString("email", null)
        val password = sharedPreferences.getString("password", null)

        if (email != null && password != null) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Logged In with Biometrics", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "No stored credentials found", Toast.LENGTH_SHORT).show()
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

    override fun onDestroy() {
        coroutineScope.cancel()
        super.onDestroy()
    }
}
