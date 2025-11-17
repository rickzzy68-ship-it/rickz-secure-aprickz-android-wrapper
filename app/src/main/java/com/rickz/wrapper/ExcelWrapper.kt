package com.rickz.wrapper

import android.content.Context
import android.webkit.WebView
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

class ExcelWrapper(private val context: Context) {

    private val salt = "EartNoun25"

    // Compute SHA-256(deviceId + salt)
    fun computeDeviceHash(deviceId: String): String {
        val input = deviceId + salt
        val bytes = input.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    // Copy XLSX from assets → internal storage
    fun copyExcelFile(): File {
        val input = context.assets.open("noun worksheet.xlsx")
        val outputFile = File(context.filesDir, "noun_worksheet.xlsx")

        FileOutputStream(outputFile).use { output ->
            input.copyTo(output)
        }

        return outputFile
    }

    // Open the Excel file inside a WebView using Google Docs Viewer
    fun openInWebView(webView: WebView, file: File) {
        val url = "https://docs.google.com/gview?embedded=1&url=" +
                file.toURI().toURL().toString()

        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
    }
}
