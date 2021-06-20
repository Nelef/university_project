package kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.*
import kr.ac.kumoh.ce.university_project_note_ver1.R
import java.text.SimpleDateFormat
import java.util.*

class UpdateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)  // TitleBar 제거
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        val uid = intent.getIntExtra("uid", 0)
        var content: String = "${intent.getStringExtra("content")}"
        var ymd = intent.getIntExtra("ymd", 20210620)
        var extraTime = intent.getLongExtra("time", 0)
        Log.d("uid1", uid.toString())

        var uEditText = findViewById<EditText>(R.id.uEditText)
        uEditText.setText(content)

        // ---------------- 스피너로 날짜 선택 ----------------
        val uDatePicker = findViewById<DatePicker>(R.id.uDatePicker)
        val uTimePicker = findViewById<TimePicker>(R.id.uTimePicker)
        val updateButton = findViewById<Button>(R.id.updateButton)

        var mYear = ymd / 10000
        var mMonth = ymd % 10000 / 100 - 1
        var mDay =  ymd % 100
        var mHour = SimpleDateFormat("HH", Locale.getDefault()).format(extraTime)
        var mMinute = SimpleDateFormat("mm", Locale.getDefault()).format(extraTime)

        uDatePicker.init(mYear, mMonth, mDay, DatePicker.OnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
            mYear = year
            mMonth = monthOfYear
            mDay = dayOfMonth
        })

        uTimePicker.hour = mHour.toInt()
        uTimePicker.minute = mMinute.toInt()
        uTimePicker.setIs24HourView(true)
        uTimePicker.setOnTimeChangedListener { view, hour, minute ->
            if(hour < 10)
                mHour = "0$hour"
            else
                mHour = "$hour"
            if(minute < 10)
                mMinute = "0$minute"
            else
                mMinute = "$minute"
        }

        updateButton.setOnClickListener {
            val intent = Intent()
            var tempDay = mDay.toString()
            var tempMonth = "$mMonth"
            if (mDay < 10)
                tempDay = "0$mDay"
            if (mMonth+1 < 10)
                tempMonth = "0${mMonth+1}"
            var date = SimpleDateFormat("yyyy:MM:dd HH:mm", Locale.getDefault()).parse("$mYear:$tempMonth:$tempDay $mHour:$mMinute")
            Log.d("uid_time", "$mYear:$tempMonth:$tempDay $mHour:$mMinute")
            intent.putExtra("yyyyMMdd", mYear*10000+(mMonth+1)*100+mDay)
            intent.putExtra("time", date.time)
            intent.putExtra("uid", uid)
            intent.putExtra("content", uEditText.text.toString())
            setResult(RESULT_OK, intent)
            finish()
        }
    }


}