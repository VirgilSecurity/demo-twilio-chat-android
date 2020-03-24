package com.virgilsecurity.android.virgil

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.virgilsecurity.android.bcommon.data.helper.virgil.VirgilHelper
import com.virgilsecurity.android.bcommon.util.JsonUtils
import com.virgilsecurity.android.bcommon.util.MessageUtils
import com.virgilsecurity.android.feature_contacts.data.repository.ContactsRepository
import com.virgilsecurity.sdk.utils.ConvertionUtils
import org.koin.core.KoinComponent
import org.koin.core.inject


class NotificationGenie : FirebaseMessagingService(), KoinComponent {
    private val virgilHelper: VirgilHelper by inject()
    private val contactsRepository: ContactsRepository by inject()

    private val channelId = "channel_id"
    private var notificationId = 0
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        //super.onMessageReceived(remoteMessage);
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        val payload = remoteMessage.data["ciphertext"] ?: return super.onMessageReceived(remoteMessage)
        val map = JsonUtils.stringToMap(ConvertionUtils.base64ToString(payload))

        val plaintext = MessageUtils.getMessageText(
                MessageUtils.mapToMessage(map, "", "", ""),
                virgilHelper
        )

        val sender = remoteMessage.data["title"] as String

        try {
            val res = contactsRepository.addContact(sender).blockingGet()
            Log.d(TAG, "Added new channel: ${res.sid}")
        } catch(e: Exception) {
            Log.d(TAG, "Failed adding new channel: $e")
        }

        sendNotification(remoteMessage.data["title"], plaintext)
        Log.d(TAG, "From: " + remoteMessage.from)
        Log.d(TAG, "Notification Message Body: " + remoteMessage.data)
    }

    private fun sendNotification(title: String?, messageBody: String) {
        val intent = Intent(applicationContext, SplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.icon_big)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
        val notificationManager = NotificationManagerCompat.from(this)
        if (notificationManager.getNotificationChannel(channelId) == null) {
            createNotificationChannel()
        }

        notificationId += 2
        notificationManager.notify(notificationId, builder.build())
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "channel_name"
            val description = "channel_description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onNewToken(s: String) {
        // TODO: register new token here
        super.onNewToken(s)
    }

    companion object {
        private const val TAG = "PUSH_GENIE"
    }
}