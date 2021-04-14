package kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline

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
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kr.ac.kumoh.ce.university_project_note_ver1.R
import kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.model.Note

class TimelineFragment : Fragment() {

//    private lateinit var galleryViewModel: GalleryViewModel

    // DB에 저장된 노트의 개수
    private var noteCount:Int = 0
    // DB에 저장된 노트의 리스트
    private lateinit var noteList:List<Note>
    // DB 변수
    lateinit var db: AppDatabase

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
//        galleryViewModel =
//                ViewModelProvider(this).get(GalleryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_timeline, container, false)

        val addButton:Button = root.findViewById(R.id.addButton)
        val noteEditText:EditText = root.findViewById(R.id.noteEditText)

        // RecyclerView에 들어갈 List
        val list = ArrayList<String>()

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
            noteList = db.noteDao().getAll()
            for (i in 0 until noteCount){
                list.add(noteList[i].content.toString())
            }
        }).start()

        // 노트 추가
        addButton.setOnClickListener{
            val text = noteEditText.text.toString()

            // 입력칸이 빈 경우
            if(text == "") return@setOnClickListener

            // DB에 메모 추가
            Thread(Runnable {
                db.noteDao().insertNote(Note(null, text))
            }).start()
            noteCount++

            // List에 EditText 내용 추가
            list.add(text)

            // EditText 초기화
            noteEditText.setText("")

            // 키보드 내리기
            val mInputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            mInputMethodManager.hideSoftInputFromWindow(noteEditText.getWindowToken(), 0)
        }

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        val recyclerView:RecyclerView = root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(root.context)

        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
        val adapter = SimpleTextAdapter(list)
        recyclerView.adapter = adapter




//        //google cloud test
//        val main2 = findViewById<Button>(R.id.button_main2)
//        main2.setOnClickListener {
//            val intent = Intent(this, MainActivity2::class.java)
//            startActivity(intent)
//        }



        val button_add_memo:Button = root.findViewById(R.id.button_add_memo)
        button_add_memo.setOnClickListener {
            // 메모 추가 버튼 (실험중)
            val intent: Intent = Intent(root.context, Memo_Input_Activity::class.java)
            startActivity(intent)
        }

        val button_search_memo:Button = root.findViewById(R.id.button_search_memo)
        button_search_memo.setOnClickListener {
            // 메모 검색 버튼 (실험중)
        }

        return root
    }
}