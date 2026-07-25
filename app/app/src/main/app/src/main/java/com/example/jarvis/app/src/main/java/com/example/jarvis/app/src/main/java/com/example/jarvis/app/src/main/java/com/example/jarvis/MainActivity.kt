package com.example.jarvis

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val requiredPermissions = mutableListOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_CONTACTS
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toTypedArray()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.all { it }) {
            startAssistantService()
            statusText.text = "Джарвис запущен и слушает в фоне.\nСкажи: \"Джарвис, открой ютуб\""
        } else {
            statusText.text = "Нужны все разрешения, чтобы ассистент работал. Открой настройки приложения и включи их вручную."
        }
    }

    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        val startButton: Button = findViewById(R.id.startButton)

        startButton.setOnClickListener {
            checkPermissionsAndStart()
        }
    }

    private fun checkPermissionsAndStart() {
        val missing = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isEmpty()) {
            startAssistantService()
            statusText.text = "Джарвис запущен и слушает в фоне.\nСкажи: \"Джарвис, открой ютуб\""
        } else {
            permissionLauncher.launch(missing.toTypedArray())
        }
    }

    private fun startAssistantService() {
        val serviceIntent = Intent(this, VoiceAssistantService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }
}
