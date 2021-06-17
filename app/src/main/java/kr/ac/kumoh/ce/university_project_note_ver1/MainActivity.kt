package kr.ac.kumoh.ce.university_project_note_ver1

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
import android.Manifest

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val channelID = "com.anushka.notificationdemo.channel1"

    var permission_list = arrayOf<String>(
//        Manifest.permission.INTERNET,
//        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
//        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

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
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNNEL_ID, CHANNEL_NAME, importance)
            mChannel.description = CHANNEL_DESC
            mNotificationManager.createNotificationChannel(mChannel)
        }

        checkPermission()

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

    //권한 설정
    fun checkPermission() {
        //현재 안드로이드 버전이 6.0미만이면 메서드를 종료한다.
        //안드로이드6.0 (마시멜로) 이후 버전부터 유저 권한설정 필요
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        for (permission in permission_list) {
            //권한 허용 여부를 확인한다.
            val chk = checkCallingOrSelfPermission(permission)
            if (chk == PackageManager.PERMISSION_DENIED) {
                //권한 허용을여부를 확인하는 창을 띄운다
                requestPermissions(permission_list, 0)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            for (i in grantResults.indices) {
                //허용됬다면
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    //권한을 하나라도 허용하지 않는다면 앱 종료
                    Toast.makeText(applicationContext, "앱권한설정하세요", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    //reply test
    fun displayNotification() {
        //Pending intent for a notification button help
        val helpPendingIntent = PendingIntent.getBroadcast(
            this@MainActivity,
            REQUEST_CODE_HELP,
            Intent(this@MainActivity, NotificationReceiver::class.java).putExtra(KEY_INTENT_HELP, REQUEST_CODE_HELP),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        //We need this object for getting direct input from notification
        val remoteInput = RemoteInput.Builder(NOTIFICATION_REPLY)
            .setLabel("메모를 입력하세요..")
            .build()

        //For the remote input we need this action object
        val action = NotificationCompat.Action.Builder(android.R.drawable.ic_delete,
            "메모를 입력하세요..", helpPendingIntent)
            .addRemoteInput(remoteInput)
            .build()

        //Creating the notifiction builder object
        val mBuilder = NotificationCompat.Builder(this, CHANNNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("TIMELINE 간편메모")
            .setContentText("메모입력")
            .setContentIntent(helpPendingIntent)
            .addAction(action)
            .setOngoing(true) // 사용자가 직접 못지우게 계속 실행하기.

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

object ThemeUtil {
    const val LIGHT_MODE = "light"
    const val DARK_MODE = "dark"
    const val DEFAULT_MODE = "default"
    fun applyTheme(themeColor: String?) {
        when (themeColor) {
            LIGHT_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            DARK_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else ->                 // 안드로이드 10 이상
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
        }
    }
}