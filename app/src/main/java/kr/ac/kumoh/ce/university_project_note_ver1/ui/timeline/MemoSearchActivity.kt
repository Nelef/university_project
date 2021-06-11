package kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import kr.ac.kumoh.ce.university_project_note_ver1.R

class MemoSearchActivity : AppCompatActivity() {

//    lateinit var searchEditText:EditText
//    lateinit var searchButton: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)  // TitleBar 제거
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_search)


        val searchEditText = this.findViewById<EditText>(R.id.searchEditText)
        val searchButton = this.findViewById<Button>(R.id.searchButton)

        searchButton.setOnClickListener {
            val intent = Intent()
            intent.putExtra("searching", searchEditText.text)
            setResult(RESULT_OK, intent)
            finish()
        }



    }
}