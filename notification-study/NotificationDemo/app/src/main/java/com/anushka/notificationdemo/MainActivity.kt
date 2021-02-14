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
import androidx.core.app.RemoteInput
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val channelID = "com.anushka.notificationdemo.channel1"
    private var notificationManager: NotificationManager? = null
    private val KEY_REPLY = "key_reply"

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
        //알림의 탭 작업 설정 -----------------------------------------------------------------------
        val tapResultIntent = Intent(this, SecondActivity::class.java).apply {
            //현재 액티비티에서 새로운 액티비티를 실행한다면 현재 액티비티를 새로운 액티비티로 교체하는 플래그
            //flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
            //이전에 실행된 액티비티들을 모두 없엔 후 새로운 액티비티 실행 플래그
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            tapResultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        //알림의 탭 작업 설정(Reply 용)--------------------------------------------------------------
        val replyResultIntent = Intent(this, ReplyActivity::class.java)
        val replyPendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            replyResultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        //바로 답장 작업 추가(reply action)
        val remoteInput: RemoteInput = RemoteInput.Builder(KEY_REPLY).run {
            setLabel("Insert you name here") //텍스트 입력 힌트
            build()
        }

        val replyAction: NotificationCompat.Action = NotificationCompat.Action.Builder(
            0, //icon
            "REPLY", //title
            replyPendingIntent
        ).addRemoteInput(remoteInput)
            .build()
        //작업 버튼 추가(action button 1)-----------------------------------------------------------
        val intent2 = Intent(this, DetailsActivity::class.java)
        val pendingIntent2: PendingIntent = PendingIntent.getActivity(
            this,
            0, //request code
            intent2,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val action2: NotificationCompat.Action =
            NotificationCompat.Action.Builder(0, "Details", pendingIntent2).build()
        // 작업 버튼 추가(action button 2)
        val intent3 = Intent(this, SettingActivity::class.java)
        val pendingIntent3: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent3,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val action3: NotificationCompat.Action =
            NotificationCompat.Action.Builder(0, "Settings", pendingIntent3).build()

        //노티피케이션 생성 -------------------------------------------------------------------------
        val notification: Notification = NotificationCompat.Builder(this@MainActivity, channelID)
            .setContentTitle("Demo Title") // 노티 제목
            .setContentText("This is a demo notification") // 노티 내용
            .setSmallIcon(android.R.drawable.ic_dialog_info) //아이콘이미지
            .setAutoCancel(true) // 사용자가 알림을 탭하면 자동으로 알림을 삭제합니다.
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) //노티클릭시 인텐트작업
            .addAction(action2) //액션버튼 인텐트
            .addAction(action3)
            .addAction(replyAction) //바로 답장 작업 추가(reply action) 액션버튼
            .build()
        /* 3. 알림 표시*///---------------------------------------------------------------------------
        //NotificationManagerCompat.notify()에 전달하는 알림 ID를 저장해야 합니다.
        // 알림을 업데이트하거나 삭제하려면 나중에 필요하기 때문입니다.
        notificationManager?.notify(notificationId, notification) //노티실행

    }

    /* 2. 채널 만들기 및 중요도 설정*/
    private fun createNotificationChannel(id: String, name: String, channelDescription: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //중요도
            val importance = NotificationManager.IMPORTANCE_HIGH
            //채널 생성
            val channel = NotificationChannel(id, name, importance).apply {
                description = channelDescription
            }
            notificationManager?.createNotificationChannel(channel)
        } else {

        }

    }

}
