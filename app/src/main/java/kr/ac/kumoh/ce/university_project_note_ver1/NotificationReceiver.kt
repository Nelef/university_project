package kr.ac.kumoh.ce.university_project_note_ver1

import android.R
import android.app.NotificationManager
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
                "noteDBa4d5aa"
            ).build()

            Thread(Runnable {
                db.noteDao().insertNote(Note(null, false, name.toString(), SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date).toInt(), date, ""))
                Log.d("알림", name.toString())
            }).start()



            //updating the notification with the input value
            val mBuilder = NotificationCompat.Builder(context, MainActivity.CHANNNEL_ID)
                .setSmallIcon(R.drawable.ic_menu_info_details)
                .setContentTitle("Hey Thanks, $name")
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(MainActivity.NOTIFICATION_ID, mBuilder.build())

        }

        //if help button is clicked
        if (intent.getIntExtra(MainActivity.KEY_INTENT_HELP, -1) == MainActivity.REQUEST_CODE_HELP) {
            Toast.makeText(context, "You Clicked Help", Toast.LENGTH_LONG).show();
        }
    }
}
