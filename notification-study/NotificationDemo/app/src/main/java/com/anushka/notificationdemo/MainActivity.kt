package com.anushka.notificationdemo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val channelID = "com.anushka.notificationdemo.channel1"
    private var notificationManager: NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //노티피케이션 매니저 서비스
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //노티피케이션 채널 생성
        createNotificationChannel(channelID, "DemoChannel", "this is a demo")
        button.setOnClickListener {
            //
            displayNotification()
        }
    }

    private fun displayNotification() {
        /* 1. 알림콘텐츠 설정*/
        //채널 ID
        val notificationId = 45
        //알림의 탭 작업 설정
        val tapResultIntent = Intent(this, SecondActivity::class.java).apply {
            //현재 액티비티에서 새로운 액티비티를 실행한다면 현재 액티비티를 새로운 액티비티로 교체하는 플래그
            flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
            //이전에 실행된 액티비티들을 모두 없엔 후 새로운 액티비티 실행 플래그
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            tapResultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        //노티피케이션 생성
        val notification: Notification = NotificationCompat.Builder(this@MainActivity, channelID)
            .setContentTitle("Demo Title")
            .setContentText("This is a demo notification")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true) // 사용자가 알림을 탭하면 자동으로 알림을 삭제합니다.
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()
        /* 3. 알림 표시*/
        notificationManager?.notify(notificationId, notification) //노티실행

    }

    /* 2. 채널 만들기 및 중요도 설정*/
    private fun createNotificationChannel(id: String, name: String, channelDescription: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //버전별 분기처리/Crash방지
            //중요도
            val importance = NotificationManager.IMPORTANCE_HIGH
            //채널 생성
            val channel = NotificationChannel(id, name, importance).apply {
                description = channelDescription
            }
            notificationManager?.createNotificationChannel(channel)

        }

    }

}
