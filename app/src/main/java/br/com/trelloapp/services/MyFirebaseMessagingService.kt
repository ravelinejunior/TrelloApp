package br.com.trelloapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import br.com.trelloapp.R
import br.com.trelloapp.firebase.FirestoreClass
import br.com.trelloapp.ui.MainActivity
import br.com.trelloapp.ui.SignInActivity
import br.com.trelloapp.utils.Constants.FCM_KEY_MESSAGE
import br.com.trelloapp.utils.Constants.FCM_KEY_TITLE
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.i(
            "TagOnMessageReceived",
            "onMessageReceived: From ${remoteMessage.from} / to ${remoteMessage.to}"
        )

        remoteMessage.data.isNotEmpty().let {
            Log.i("TagOnMessageReceived", "onMessageReceived: ${remoteMessage.data}")

            val title = remoteMessage.data[FCM_KEY_TITLE]!!
            val message = remoteMessage.data[FCM_KEY_MESSAGE]!!

            sendNotification(title, message)


        }

        remoteMessage.notification?.let {
            Log.i("TagOnMessageReceived", "onMessageReceived:${it.body}")

        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("TagOnMessageReceived", "onNewToken: $token")
        sendRegistationToServer(token)
    }

    private fun sendRegistationToServer(token: String?) {

    }

    private fun sendNotification(title: String, messageBody: String) {
        val intent = if (FirestoreClass().getCurrentUserId().isNotEmpty()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, SignInActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelId = this.resources.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)

        notificationBuilder.setSmallIcon(R.mipmap.my_launcher)
        notificationBuilder.setContentTitle(title)
        notificationBuilder.setContentText(messageBody)
        notificationBuilder.setAutoCancel(true)
        notificationBuilder.setSound(defaultSoundUri)
        notificationBuilder.setContentIntent(pendingIntent)

        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                channelId,
                "Channel OrganizeIt",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}