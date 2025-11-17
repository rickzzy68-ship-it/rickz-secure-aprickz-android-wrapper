package com.rickz.wrapper

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var excelWrapper: ExcelWrapper

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        excelWrapper = ExcelWrapper(this)

        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val computedHash = excelWrapper.computeDeviceHash(deviceId)

        val inputCode = findViewById<EditText>(R.id.inputCode)
        val activateBtn = findViewById<Button>(R.id.activateBtn)
        val webView = findViewById<WebView>(R.id.webView)

        activateBtn.setOnClickListener {
            val entered = inputCode.text.toString().trim()

            if (entered == computedHash) {
                Toast.makeText(this, "Activation Successful!", Toast.LENGTH_LONG).show()

                val file = excelWrapper.copyExcelFile()
                excelWrapper.openInWebView(webView, file)

            } else {
                Toast.makeText(this, "Invalid Code. This device is not registered.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
