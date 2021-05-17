package kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.CalendarView
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import kr.ac.kumoh.ce.university_project_note_ver1.R
import java.text.SimpleDateFormat
import java.util.*

class TimelineCalendarActivity2 : AppCompatActivity() {

    lateinit var calendarView: CalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline_calendar2)



        var vDatePicker = findViewById<DatePicker>(R.id.vDatePicker)
        var vDateEnter = findViewById<Button>(R.id.vDateEnter)

        val cal = Calendar.getInstance()
        var mYear = cal.get(Calendar.YEAR).toString()
        var mMonth = cal.get(Calendar.MONTH).toString()
        var mDay = cal.get(Calendar.DATE).toString()

        vDatePicker.init(mYear.toInt(), mMonth.toInt(), mDay.toInt(), DatePicker.OnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
            mYear = year.toString()
            mMonth = (monthOfYear).toString()
            mDay = dayOfMonth.toString()
        })

        vDateEnter.setOnClickListener {
            var intent = Intent()
            intent.putExtra("year", mYear)
            intent.putExtra("month", (mMonth.toInt()+1).toString())
            intent.putExtra("dayOfMonth", mDay)
            setResult(RESULT_OK, intent)
            finish()
        }

        // ---------------- 달력으로 선택 ----------------
        calendarView = findViewById(R.id.calendarView)
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val intent = Intent()
            val temp = month+1
            intent.putExtra("year", year.toString())
            intent.putExtra("month", temp.toString())
            intent.putExtra("dayOfMonth", dayOfMonth.toString())
            setResult(RESULT_OK, intent)
            finish()
        }
    }


}