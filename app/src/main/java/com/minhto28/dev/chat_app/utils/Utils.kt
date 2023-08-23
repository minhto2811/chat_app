package com.minhto28.dev.chat_app.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.icu.text.Transliterator
import java.util.UUID
import kotlin.math.abs

fun generateUniqueID(): String {
    val uuid = UUID.randomUUID()
    val mostSignificantBits = uuid.mostSignificantBits
    val uid = abs(mostSignificantBits % 100000000)
    return uid.toString().padStart(8, '0')
}

fun showMessage(
    message: String, context: Context, cancel: Boolean, callbacks: (() -> Unit)?
) {
    val dialog = AlertDialog.Builder(context)
    dialog.setTitle("Notification")
    dialog.setMessage(message)
    dialog.setCancelable(false)
    if (cancel) {
        dialog.setNegativeButton("Cancel", null)
    }
    dialog.setPositiveButton("Confirm", DialogInterface.OnClickListener { _, _ ->
        callbacks?.invoke()
    })
    dialog.create().show()
}


@SuppressLint("NewApi")
fun removeDiacritics(input: String): String {
    val transliterator = Transliterator.getInstance("NFD; [:Nonspacing Mark:] Remove; NFC")
    return transliterator.transliterate(input)
}

