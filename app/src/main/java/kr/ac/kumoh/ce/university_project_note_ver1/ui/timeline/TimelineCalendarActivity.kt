package kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.CalendarView
import android.widget.DatePicker
import kr.ac.kumoh.ce.university_project_note_ver1.R
import java.util.*

class TimelineCalendarActivity : AppCompatActivity() {

    lateinit var calendarView: CalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)  // TitleBar 제거
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline_calendar)

        // ---------------- 스피너로 날짜 선택 ----------------
        val vDatePicker = findViewById<DatePicker>(R.id.vDatePicker)
        val vDateEnter = findViewById<Button>(R.id.vDateEnter)

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
            val intent = Intent()
            intent.putExtra("year", mYear)
            intent.putExtra("month", (mMonth.toInt()+1).toString())
            intent.putExtra("dayOfMonth", mDay)
            setResult(RESULT_OK, intent)
            finish()
        }

        // ---------------- 달력으로 날짜 선택 ----------------
        calendarView = findViewById(R.id.calendarView)
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val intent = Intent()
            intent.putExtra("year", year.toString())
            intent.putExtra("month", (month+1).toString())
            intent.putExtra("dayOfMonth", dayOfMonth.toString())
            setResult(RESULT_OK, intent)
            finish()
        }
    }


}