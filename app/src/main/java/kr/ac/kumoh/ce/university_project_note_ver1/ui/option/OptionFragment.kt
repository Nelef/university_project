package kr.ac.kumoh.ce.university_project_note_ver1.ui.option

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import kr.ac.kumoh.ce.university_project_note_ver1.R
import java.text.SimpleDateFormat
import java.util.*

class OptionFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_option, container, false)





        return root
    }
}