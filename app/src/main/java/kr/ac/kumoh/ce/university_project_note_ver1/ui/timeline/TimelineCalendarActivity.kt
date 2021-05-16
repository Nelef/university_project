package kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.Toast
import kr.ac.kumoh.ce.university_project_note_ver1.R

class TimelineCalendarActivity : AppCompatActivity() {

    lateinit var calendarView:CalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline_calendar)

        calendarView = findViewById(R.id.calendarView_a)
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