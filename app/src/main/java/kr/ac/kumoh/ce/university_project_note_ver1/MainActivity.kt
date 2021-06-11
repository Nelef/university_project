package kr.ac.kumoh.ce.university_project_note_ver1

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    var mBackWait:Long = 0

    private val channelID = "com.anushka.notificationdemo.channel1"
    private var notificationManager: NotificationManager? = null
    private val KEY_REPLY = "key_reply"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_timeline, R.id.nav_map, R.id.nav_googledrive, R.id.nav_option), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //reply test
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNNEL_ID, CHANNEL_NAME, importance)
            mChannel.description = CHANNEL_DESC
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mNotificationManager.createNotificationChannel(mChannel)
        }
        displayNotification()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    //reply test
    fun displayNotification() {

        //Pending intent for a notification button help
        val helpPendingIntent = PendingIntent.getBroadcast(
            this@MainActivity,
            REQUEST_CODE_HELP,
            Intent(this@MainActivity, NotificationReceiver::class.java)
                .putExtra(KEY_INTENT_HELP, REQUEST_CODE_HELP),
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        //We need this object for getting direct input from notification
        val remoteInput = RemoteInput.Builder(NOTIFICATION_REPLY)
            .setLabel("Please enter your name")
            .build()


        //For the remote input we need this action object
        val action = NotificationCompat.Action.Builder(android.R.drawable.ic_delete,
            "Reply Now...", helpPendingIntent)
            .addRemoteInput(remoteInput)
            .build()

        //Creating the notifiction builder object
        val mBuilder = NotificationCompat.Builder(this, CHANNNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("Hey this is Simplified Coding...")
            .setContentText("Please share your name with us")
            .setAutoCancel(true)
            .setContentIntent(helpPendingIntent)
            .addAction(action)


        //finally displaying the notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build())
    }

    companion object {
        const val NOTIFICATION_REPLY = "NotificationReply"
        const val CHANNNEL_ID = "SimplifiedCodingChannel"
        const val CHANNEL_NAME = "SimplifiedCodingChannel"
        const val CHANNEL_DESC = "This is a channel for Simplified Coding Notifications"
        const val KEY_INTENT_MORE = "keyintentmore"
        const val KEY_INTENT_HELP = "keyintenthelp"
        const val REQUEST_CODE_MORE = 100
        const val REQUEST_CODE_HELP = 101
        const val NOTIFICATION_ID = 200
    }

}