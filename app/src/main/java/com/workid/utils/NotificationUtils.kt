package com.workid.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Looper
import android.os.Vibrator
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.workid.R
import com.workid.activities.call.IncomingInvitationActivity
import com.workid.broadcast.ResponseReceiver
import com.workid.models.RequestCallItems
import com.workid.utils.Constant.Companion.CHANNEL_ID
import com.workid.utils.Constant.Companion.CHANNEL_NAME
import com.workid.utils.Constant.Companion.NOTIFICATION_REQUEST_CALL_DURATION
import com.workid.utils.Constant.Companion.NOTIFICATION_REQUEST_CALL_ID
import com.workid.utils.Constant.Companion.NOTIFICATION_VIBRATE_PATTERN
import com.workid.utils.Constant.Companion.REMOTE_NOTIFICATION_ACCEPTED
import com.workid.utils.Constant.Companion.REMOTE_NOTIFICATION_REJECTED
import com.workid.utils.IntentUtils.addDataIntoIntent
import java.util.*


fun Service.showNotificationWithFullScreenIntent(
    data: RequestCallItems,
    channelId: String = CHANNEL_ID,
) {
    //Handle Fullscreen Intent - Screen locked
    val lockedScreenAction = Intent(this, IncomingInvitationActivity::class.java)
    lockedScreenAction.addDataIntoIntent(data)
    lockedScreenAction.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    //Handle click on notification - Accept
    val receiveCallAction = Intent(this, ResponseReceiver::class.java)
    receiveCallAction.putExtra("id", NOTIFICATION_REQUEST_CALL_ID)
    receiveCallAction.addDataIntoIntent(data)
    receiveCallAction.action = REMOTE_NOTIFICATION_ACCEPTED

    //Handle click on notification - Decline
    val cancelCallAction = Intent(this, ResponseReceiver::class.java)
    cancelCallAction.putExtra("id", NOTIFICATION_REQUEST_CALL_ID)
    cancelCallAction.addDataIntoIntent(data)
    cancelCallAction.action = REMOTE_NOTIFICATION_REJECTED

    val receiveCallPendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        1200,
        receiveCallAction, PendingIntent.FLAG_UPDATE_CURRENT
    )
    val cancelCallPendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        1201,
        cancelCallAction,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    val fullscreenIntent = PendingIntent.getActivity(
        this,
        1202,
        lockedScreenAction,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    //Customize UI of notification
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val avatar = loadImageToNotificationAvatar(data.callerPhotoURL)
    //Custom vibration for notification
    val vibrationEffect = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
//        vibrationEffect
//            .vibrate(VibrationEffect.createWaveform(NOTIFICATION_VIBRATE_PATTERN,3))
//    }else{
//        @Suppress("DEPRECATION")
//        vibrationEffect.vibrate(NOTIFICATION_VIBRATE_PATTERN, 3)
//    }

    val remoteView = RemoteViews(packageName, R.layout.holder_call_notification)
    if (avatar != null) {
        remoteView.setImageViewBitmap(R.id.noti_avatar, avatar)
    } else {
        remoteView.setImageViewResource(R.id.noti_avatar, R.drawable.ic_avatar_default)
    }

    remoteView.setTextViewText(R.id.noti_name, data.callerName)
    remoteView.setTextViewText(
        R.id.noti_message,
        getString(
            R.string.notification_call_message,
            data.meetingType!!.capitalize(Locale.getDefault())
        )
    )
    remoteView.setOnClickPendingIntent(R.id.noti_accept, receiveCallPendingIntent)
    remoteView.setOnClickPendingIntent(R.id.noti_decline, cancelCallPendingIntent)

    Looper.prepare()
    val builder = NotificationCompat.Builder(this, channelId)
        .setCustomHeadsUpContentView(remoteView)
        .setSmallIcon(android.R.drawable.arrow_up_float)
        .setContent(remoteView)
        .setCategory(NotificationCompat.CATEGORY_CALL)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setVibrate(NOTIFICATION_VIBRATE_PATTERN)
        .setTimeoutAfter(NOTIFICATION_REQUEST_CALL_DURATION.toLong())
//        .setSound(Uri.parse("android.resource://" + applicationContext.packageName + "/" + R.raw.funny_indian))
        .setDeleteIntent(cancelCallPendingIntent)
        .setFullScreenIntent(fullscreenIntent, true)

    with(notificationManager) {
        buildChannel()
        val notification = builder.build()
        notify(NOTIFICATION_REQUEST_CALL_ID, notification)
    }

}

fun Context.showIncomingInvitationActivity(data: RequestCallItems) {
    val intent = Intent(this, IncomingInvitationActivity::class.java)
    intent.addDataIntoIntent(data)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

private fun NotificationManager.buildChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val descriptionText = "Handle call notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
            description = descriptionText
        }
        createNotificationChannel(channel)
    }
}

fun Context.cancelNotification(id: Int) {
    val notificationManager =
        this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancel(id)
}

