package com.rickz.wrapper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private val SALT = "EartNoun25"      // Your secret salt
    private val EXCEL_FILE_NAME = "noun worksheet.xlsx"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etCode: EditText = findViewById(R.id.etCode)
        val btnActivate: Button = findViewById(R.id.btnActivate)
        val tvError: TextView = findViewById(R.id.tvError)

        // If already activated → open Excel immediately
        if (isActivated()) {
            openExcelFile()
            return
        }

        btnActivate.setOnClickListener {
            val code = etCode.text.toString().trim()
            val deviceId = getDeviceId()

            if (code.isEmpty()) {
                tvError.text = "Please enter a code"
                tvError.visibility = TextView.VISIBLE
                return@setOnClickListener
            }

            val generated = sha256(code + SALT + deviceId)

            if (generated == createStoredHash(code)) {
                saveActivation(code)
                openExcelFile()
            } else {
                tvError.text = "Invalid activation code"
                tvError.visibility = TextView.VISIBLE
            }
        }
    }

    // Save activation locally (offline forever)
    private fun saveActivation(code: String) {
        val deviceId = getDeviceId()
        val hash = sha256(code + SALT + deviceId)

        val file = File(filesDir, "activation.dat")
        file.writeText(hash)
    }

    // Check if already activated
    private fun isActivated(): Boolean {
        val file = File(filesDir, "activation.dat")
        return file.exists()
    }

    // Create the expected hash (admin-generated)
    private fun createStoredHash(code: String): String {
        val deviceId = getDeviceId()
        return sha256(code + SALT + deviceId)
    }

    // Get a stable device ID
    private fun getDeviceId(): String {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }

    // SHA-256 hashing
    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // Copy and open your Excel file
    private fun openExcelFile() {
        val inputStream = assets.open(EXCEL_FILE_NAME)
        val outFile = File(filesDir, EXCEL_FILE_NAME)
        val outputStream = FileOutputStream(outFile)

        inputStream.copyTo(outputStream)

        inputStream.close()
        outputStream.close()

        val uri = Uri.fromFile(outFile)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)

        finish()
    }
}
