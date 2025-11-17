package com.rickz.wrapper

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private val salt = "EartNoun25"   // YOUR SALT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputCode = findViewById<EditText>(R.id.inputCode)
        val btnActivate = findViewById<Button>(R.id.btnActivate)

        btnActivate.setOnClickListener {
            val userCode = inputCode.text.toString().trim()
            if (userCode.isEmpty()) {
                Toast.makeText(this, "Enter activation code.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val deviceId = getDeviceId()
            val correctCode = sha256(deviceId + salt)

            if (correctCode.equals(userCode, ignoreCase = true)) {
                Toast.makeText(this, "Activation successful!", Toast.LENGTH_SHORT).show()
                openExcelFile()
            } else {
                Toast.makeText(this, "Invalid code.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("HardwareIds")
    private fun getDeviceId(): String {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun openExcelFile() {
        val inputStream = assets.open("worksheet.xlsx")
        val outFile = File(cacheDir, "worksheet.xlsx")
        val outputStream = FileOutputStream(outFile)

        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(outFile), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivity(intent)
    }
}
