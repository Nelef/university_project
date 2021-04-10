package kr.ac.kumoh.s20160553.timeline_base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kr.ac.kumoh.s20160553.timeline_base.model.Note
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
    //db에 저장된 Note들 리스트
    private lateinit var noteList: List<Note>

    //db 변수
    lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // recyclerView를 위한 리스트
        val list = ArrayList<String>()

        //db 초기화
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "noteDB"
        ).build()

        //db에서 내용 불러오기
        Thread(Runnable {
            Log.d("load", "db loading")
            noteCount = db.noteDao().countNote()
            noteList = db.noteDao().getAll()
            // SharedPreference로 불러온 텍스트 하나씩 추가
            for (i in 0 until noteCount) {
                list.add(noteList[i].content.toString())
            }
        }).start()

        // 추가 버튼 클릭 시
        addButton.setOnClickListener {
            val text = noteEditText.text.toString()

            // 아무것도 입력하지 않았을 경우
            if (text == "")
                return@setOnClickListener

            //DB에 메모 추가
            Thread(Runnable {
                db.noteDao().insertNote(Note(null, text))
            }).start()

            noteCount++
            // 리스트에 EditText의 내용을 추가
            list.add(text)

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