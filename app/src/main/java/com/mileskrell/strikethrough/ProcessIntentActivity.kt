package com.mileskrell.strikethrough

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity

class ProcessIntentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val input = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)!!
        val processedText = strikeText(input)
        if (intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)) {
            val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(getString(R.string.clip_data_label), processedText)
            clipboardManager.setPrimaryClip(clip)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                Toast.makeText(this, getString(R.string.copied_to_clipboard_confirmation), Toast.LENGTH_SHORT).show()
            }
        } else {
            val resultIntent = Intent().putExtra(Intent.EXTRA_PROCESS_TEXT, processedText)
            setResult(RESULT_OK, resultIntent)
        }
        finish()
    }
}
