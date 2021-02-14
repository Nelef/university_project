package com.anushka.notificationdemo

import android.app.NotificationManager
import android.app.RemoteInput
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_reply.*

class ReplyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply)
        receiveInput()
    }

    //Remote Input 노티피케이션 인텐트 수신
    private fun receiveInput() {
        val KEY_REPLY = "key_reply"
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        remoteInput?.let {
            val text = it.getCharSequence(KEY_REPLY).toString()
            tv_reply.text = text
            receiveSuccessNoti()
        }

    }

    private fun receiveSuccessNoti() {
        val channelID = "com.anushka.notificationdemo.channel1"
        val notificationId = 45

        val repliedNotification = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentText("Your reply received")
            .build()
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, repliedNotification)
    }
}