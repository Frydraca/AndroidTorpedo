package hu.bme.aut.android.torpedo.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class notificationService : FirebaseMessagingService(){

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.w("NOTE", "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            Log.w("NOTE", "Message data payload: " + remoteMessage.data)

        }

        remoteMessage.notification?.let {
            Log.w("NOTE", "Message Notification Body: ${it.body}")
        }
    }
}