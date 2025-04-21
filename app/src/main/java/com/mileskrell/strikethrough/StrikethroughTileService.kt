package com.mileskrell.strikethrough

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.DialogInterface
import android.os.Build
import android.service.quicksettings.TileService
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged

class StrikethroughTileService : TileService() {

    override fun onClick() {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.quick_settings_dialog, null)
        var processedText = ""

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton(R.string.copy_to_clipboard_action) { _, _ ->
                val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(getString(R.string.clip_data_label), processedText)
                clipboardManager.setPrimaryClip(clip)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                    Toast.makeText(this, getString(R.string.copied_to_clipboard_confirmation), Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel_action) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .apply {
                setOnShowListener {
                    getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = false
                }
            }

        val inputEditText = view.findViewById<EditText>(R.id.dialog_input)
        val outputTextView = view.findViewById<TextView>(R.id.dialog_output)
        inputEditText.doOnTextChanged { text, _, _, _ ->
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = text?.isNotEmpty() ?: return@doOnTextChanged
            processedText = strikeText(text)
            outputTextView.text = processedText
        }

        showDialog(dialog)
    }
}
