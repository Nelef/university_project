package kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import kr.ac.kumoh.ce.university_project_note_ver1.R

class Memo_Input_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_input)

        val editText:EditText by lazy {
            findViewById(R.id.editTextTextMultiLine)
        }
        val button_confirm:Button by lazy {
            findViewById(R.id.button_confirm)
        }
        val button_cancel:Button by lazy {
            findViewById(R.id.button_cancel)
        }

        var intent:Intent = intent

        button_confirm.setOnClickListener {
            // EditText의 내용을 TimelineFragment로 전달
            var memo:String = editText.text.toString()
            intent.putExtra("memo", memo)
            setResult(RESULT_OK, intent)
            finish()        // 액티비티 종료
        }

        button_cancel.setOnClickListener {
            finish()        // 액티비티 종료
        }
    }
}