package kr.ac.kumoh.s20160553.timeline_base

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MainActivity : AppCompatActivity() {

    // 추가 버튼
    val addButton: Button by lazy {
        findViewById<Button>(R.id.addButton)
    }

    // 입력 창 (EditText)
    val noteEditText: EditText by lazy {
        findViewById<EditText>(R.id.noteEditText)
    }

    // 삭제 버튼
    val deleteButton: EditText by lazy {
        findViewById<EditText>(R.id.item_button)
    }

    private var noteCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // recyclerView를 위한 리스트
        val list = ArrayList<String>()

        // SharedPreferences
        val notePreferences = getSharedPreferences("myNote", Context.MODE_PRIVATE)
        noteCount = notePreferences.getInt("count", 0)

        // SharedPreference로 불러온 텍스트 하나씩 추가
        for( i in 0 until noteCount){
            val text = notePreferences.getString("note$i", "fuck!").toString()
            list.add(text)
        }

        // 추가 버튼 클릭 시
        addButton.setOnClickListener {
            val text = noteEditText.text.toString()

            // 아무것도 입력하지 않았을 경우
            if (text == "")
                return@setOnClickListener

            notePreferences.edit(true){
                putInt("count", ++noteCount)
                putString("note${noteCount - 1}", text)
            }

            // 리스트에 EditText의 내용을 추가
            list.add(String.format(noteEditText.text.toString()))

            // 키보드 내리기
            val mInputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            mInputMethodManager.hideSoftInputFromWindow(noteEditText.getWindowToken(), 0)

            // EditText 초기화
            noteEditText.setText("")
        }

//        deleteButton.setOnClickListener {
//
//        }

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
        val adapter = SimpleTextAdapter(list)
        recyclerView.adapter = adapter
        
        // 리사이클러뷰를 사용하는 코드는 이 아래에 작성
    }
}