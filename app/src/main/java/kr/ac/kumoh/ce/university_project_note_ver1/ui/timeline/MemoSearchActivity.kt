package kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import kr.ac.kumoh.ce.university_project_note_ver1.R

class MemoSearchActivity : AppCompatActivity() {

//    lateinit var searchEditText:EditText
//    lateinit var searchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_search)


        var searchEditText = this.findViewById<EditText>(R.id.searchEditText)
        var searchButton = this.findViewById<Button>(R.id.searchButton)

        searchButton.setOnClickListener {
            var intent = Intent()
            intent.putExtra("searching", searchEditText.text)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}