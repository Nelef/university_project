package kr.ac.kumoh.ce.university_project_note_ver1.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kr.ac.kumoh.ce.university_project_note_ver1.R
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val fragment2:Fragment

        var calendarView: CalendarView = root.findViewById<CalendarView>(R.id.calendarView)

        var button1: Button = root.findViewById<Button>(R.id.button1)
        button1.setOnClickListener {
            button1.text = convertLongToTime(calendarView.date)

        }

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            button1.text = dayOfMonth.toString()
//            var fragmentTransaction:FragmentTransaction =

        }

























        return root
    }

    // CalenderView에서 얻는 시간은 Long type의 Millisecond 단위이다.
    // 이를 다음의 Format으로 변환한다.
    fun convertLongToTime(time:Long): String{
        val date = Date(time)
        val format = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        return format.format(date)
    }
}