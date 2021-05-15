package kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kr.ac.kumoh.ce.university_project_note_ver1.R
import kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.model.Note
import java.time.LocalDateTime

class TimelineFragment : Fragment() {
    // DB에 저장된 노트의 개수
    private var noteCount:Int = 0
    // DB에 저장된 노트의 리스트
    private var noteList: MutableList<Note> = mutableListOf()
    // DB 변수
    lateinit var db: AppDatabase

    lateinit var noteEditText:EditText
    lateinit var addButton:Button
    lateinit var recyclerView:RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_timeline, container, false)

        addButton= root.findViewById(R.id.addButton)
        noteEditText = root.findViewById(R.id.noteEditText)

        // DB 초기화
        db = Room.databaseBuilder(
            root.context,
            AppDatabase::class.java,
            "noteDB"
        ).build()

        // DB에서 내용 불러오기
        Thread(Runnable {
            Log.d("load", "db loading")
            noteCount = db.noteDao().countNote()
            var tempNoteList = db.noteDao().getAll()
            for (i in 0 until noteCount) {
                noteList.add(tempNoteList[i])
                Log.d("Tag", tempNoteList[i].content!!)
            }
        }).start()

        // 노트 추가
        addButton.setOnClickListener{
            val text = noteEditText.text.toString()

            // 입력칸이 빈 경우
            if(text == "") return@setOnClickListener

            var tempNote = Note(null, false, text, 20210515, "타임스탬프")

            // DB에 메모 추가
            Thread(Runnable {
                db.noteDao().insertNote(tempNote)
            }).start()
            noteCount++
            noteList.add(tempNote)

            // EditText 초기화
            noteEditText.setText("")

            // 키보드 내리기
            val mInputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            mInputMethodManager.hideSoftInputFromWindow(noteEditText.getWindowToken(), 0)
        }

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        recyclerView = root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(root.context)

        val adapter = NoteAdapter(noteList.toList())
        recyclerView.adapter = adapter

        val button_add_memo:Button = root.findViewById(R.id.button_add_memo)
        button_add_memo.setOnClickListener {
            // 메모 추가 버튼 (실험중)
            val intent: Intent = Intent(root.context, Memo_Input_Activity::class.java)
            startActivityForResult(intent, 1)
        }

        val button_search_memo:Button = root.findViewById(R.id.button_search_memo)
        button_search_memo.setOnClickListener {
            // 메모 검색 버튼 (실험중)2
        }

        //google drive test
        val button_drive:Button = root.findViewById(R.id.button_drive)
            //findViewById<Button>(R.id.button_main2)
        button_drive.setOnClickListener {
            val intent: Intent = Intent(root.context, Drive_save_activity::class.java)
            //TODO change list to noteList
//            intent.putExtra("list", list)
            startActivity(intent)
        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==RESULT_OK){
            if(requestCode==1){
                if (data != null) {
                    val text_memo = data.getStringExtra("memo")
                    if(text_memo != ""){
                        // 입력이 빈칸이 아닐 때만 동작
                        val tempNote = Note(null, false, text_memo.toString(), 20210515, "타임스탬프")
                        noteList.add(tempNote)
                        val adapter = NoteAdapter(noteList.toList())
                        recyclerView.adapter = adapter

                        Thread(Runnable {
                            db.noteDao().insertNote(tempNote)
                        }).start()
                        noteCount++

                    }
                }
            }
        }
    }
}