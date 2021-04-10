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

    val addButton: Button by lazy {
        findViewById<Button>(R.id.addButton)
    }

    val noteEditText: EditText by lazy {
        findViewById<EditText>(R.id.noteEditText)
    }

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

        for( i in 0 until noteCount){
            val text = notePreferences.getString("note$i", "fuck!").toString()
            list.add(text)
        }

        addButton.setOnClickListener {
            val text = noteEditText.text.toString()

            notePreferences.edit(true){
                putInt("count", ++noteCount)
                putString("note${noteCount - 1}", text)
            }

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


    }

}