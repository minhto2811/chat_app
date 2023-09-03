package com.minhto28.dev.chat_app.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.core.app.NotificationCompat
import com.minhto28.dev.chat_app.R
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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
    dialog.setPositiveButton("Confirm") { _, _ ->
        callbacks?.invoke()
    }
    dialog.create().show()
}

fun hiddenSoftKeyboard(activity: Activity) {
    val imm: InputMethodManager =
        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
}


@SuppressLint("NewApi")
fun getTimeDisplay(timestamp: Long): String {
    val currentTime = System.currentTimeMillis()
    val messageTime = Instant.ofEpochMilli(timestamp)
    val currentInstant = Instant.ofEpochMilli(currentTime)

    val duration = Duration.between(messageTime, currentInstant)
    val minutesAgo = duration.toMinutes()

    return when {
        minutesAgo < 1 -> "Just now"
        minutesAgo < 60 -> "$minutesAgo min ago"
        else -> {
            val localDateTime = LocalDateTime.ofInstant(messageTime, ZoneId.systemDefault())
            localDateTime.format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"))
        }
    }
}

fun View.clickEffect() {
    val clickAnimation =
        AnimationUtils.loadAnimation(context, R.anim.click_animation)
    startAnimation(clickAnimation)
}

fun <T : Activity> changeActivity(
    context: Activity,
    targetActivity: Class<T>,
    isFinsh: Boolean = true,
    idEdit: Int? = null
) {
    val intent = Intent(context, targetActivity)
    if (idEdit != null) {
        intent.putExtra("idEdit", idEdit)
    }
    context.startActivity(intent)
    if (isFinsh)
        context.finish()
}


 fun createNotificationChannel(context: Context,channelId:String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = context.getString(R.string.app_name)
        val descriptionText = context.getString(R.string.todo)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}


 @SuppressLint("LaunchActivityFromNotification")
 fun createNotification(
     context: Context,
     channelId:String,
     title:String, message:String,
 ):NotificationCompat.Builder{
    val click = createPendingIntent(1,context)

     return NotificationCompat.Builder(context, channelId)
         .setSmallIcon(R.drawable.logo)
         .setContentTitle(title)
         .setContentText(message)
         .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setContentIntent(click)
}

fun createPendingIntent(choose:Int,context: Context):PendingIntent{
    val intent = Intent("com.example.ACTION_NOTIFICATION_CLICKED")
    intent.putExtra("choose",choose)
    val requestCode = choose
    val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)
   return pendingIntent
}









