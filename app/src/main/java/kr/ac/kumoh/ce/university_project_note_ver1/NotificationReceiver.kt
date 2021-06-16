package kr.ac.kumoh.ce.university_project_note_ver1

import android.R
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.room.Room
import kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.AppDatabase
import kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.TimelineFragment
import kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.model.Note
import java.text.SimpleDateFormat
import java.util.*

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        //getting the remote input bundle from intent
        val remoteInput = androidx.core.app.RemoteInput.getResultsFromIntent(intent)

        //if there is some input
        if (remoteInput != null) {

            //getting the input value
            val name = remoteInput.getCharSequence(MainActivity.NOTIFICATION_REPLY)
            val date = System.currentTimeMillis()

            var db = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "noteDBa4d5aaa"
            ).build()

            Thread(Runnable {
                db.noteDao().insertNote(Note(null, false, name.toString(), SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date).toInt(), date, ""))
                Log.d("알림", name.toString())
            }).start()

            val helpPendingIntent = PendingIntent.getBroadcast(
                    context,
                    MainActivity.REQUEST_CODE_HELP,
                    Intent(context, NotificationReceiver::class.java).putExtra(MainActivity.KEY_INTENT_HELP, MainActivity.REQUEST_CODE_HELP),
                    PendingIntent.FLAG_UPDATE_CURRENT
            )

            //We need this object for getting direct input from notification
            val remoteInput = androidx.core.app.RemoteInput.Builder(MainActivity.NOTIFICATION_REPLY)
                .setLabel("메모를 입력하세요..")
                .build()

            //For the remote input we need this action object
            val action = NotificationCompat.Action.Builder(android.R.drawable.ic_delete,
                "메모를 입력하세요..", helpPendingIntent)
                .addRemoteInput(remoteInput)
                .build()

            val mBuilder = NotificationCompat.Builder(context, MainActivity.CHANNNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle("TIMELINE 간편메모")
                .setContentText("메모입력")
                .setContentIntent(helpPendingIntent)
                .addAction(action)
                .setOngoing(true) // 사용자가 직접 못지우게 계속 실행하기.

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            Toast.makeText(context, "메모입력완료.. $name", Toast.LENGTH_LONG).show();
            notificationManager.notify(MainActivity.NOTIFICATION_ID, mBuilder.build())
        }
    }
}
