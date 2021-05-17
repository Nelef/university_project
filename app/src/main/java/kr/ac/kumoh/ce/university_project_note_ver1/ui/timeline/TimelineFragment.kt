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
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kr.ac.kumoh.ce.university_project_note_ver1.R
import kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.model.Note
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

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


    lateinit var calendarButton:Button
    lateinit var selected_Time: TextView
    lateinit var selected_year:String
    lateinit var selected_Month:String
    lateinit var selected_dayOfMonth:String

    var selected_Time_DB:Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_timeline, container, false)

        addButton= root.findViewById(R.id.addButton)
        noteEditText = root.findViewById(R.id.noteEditText)
        calendarButton = root.findViewById(R.id.CalendarButton)
        selected_Time = root.findViewById(R.id.selected_Time)

        // 오늘 날짜 가져오기
        val date: Date = Calendar.getInstance().time
        selected_year = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
        selected_Month = SimpleDateFormat("MM", Locale.getDefault()).format(date)
        selected_dayOfMonth = SimpleDateFormat("dd", Locale.getDefault()).format(date)
//        selected_Time.text = selected_year + "년 " + selected_Month + "월 " + selected_dayOfMonth + "일"
        selected_Time.text = getString(R.string.year_month_day, selected_year, selected_Month, selected_dayOfMonth)
        selected_Time_DB = selected_year.toInt()*10000+selected_Month.toInt()*100+selected_dayOfMonth.toInt()

        // DB 초기화
        db = Room.databaseBuilder(
            root.context,
            AppDatabase::class.java,
            "noteDBa4da"
        ).build()

        // DB에서 내용 불러오기
        Thread(Runnable {
            Log.d("load", "db loading")
            noteCount = db.noteDao().countNote2(selected_Time_DB)
            val tempNoteList = db.noteDao().getAll2(selected_Time_DB)
            for (i in 0 until noteCount) {
                noteList.add(tempNoteList[i])
                Log.d("Tag", tempNoteList[i].content!!)
            }
        }).start()

        noteList.sortByDescending { it.ymd }

        // 노트 추가
        addButton.setOnClickListener{
            val text = noteEditText.text.toString()

            // 입력칸이 빈 경우
            if(text == "") return@setOnClickListener

            val tempNote = Note(null, false, text, selected_Time_DB, "타임스탬프")
            noteList.add(tempNote)

            val adapter = NoteAdapter(noteList, db)
            recyclerView.adapter = adapter

            noteList.sortByDescending { it.ymd }

            // DB에 메모 추가
            Thread(Runnable {
                db.noteDao().insertNote(tempNote)
            }).start()
            noteCount++

            // EditText 초기화
            noteEditText.setText("")



            // 키보드 내리기
            val mInputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            mInputMethodManager.hideSoftInputFromWindow(noteEditText.getWindowToken(), 0)
        }

        calendarButton.setOnClickListener {
            val intent = Intent(root.context, TimelineCalendarActivity2::class.java)
            startActivityForResult(intent, 2)
        }

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        recyclerView = root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(root.context)

//        val adapter = NoteAdapter(noteList.toList())
        val adapter = NoteAdapter(noteList, db)
        recyclerView.adapter = adapter

        val button_add_memo:Button = root.findViewById(R.id.button_add_memo)
        button_add_memo.setOnClickListener {
            // 메모 추가 버튼 (실험중)
            val intent = Intent(root.context, Memo_Input_Activity::class.java)
            startActivityForResult(intent, 1)
        }

        val button_search_memo:Button = root.findViewById(R.id.button_search_memo)
        button_search_memo.setOnClickListener {
            // 메모 검색 버튼 (실험중)2
            val intent = Intent(root.context, MemoSearchActivity::class.java)
            startActivityForResult(intent, 3)
        }

        //google drive test
        val button_drive:Button = root.findViewById(R.id.button_drive)
        button_drive.setOnClickListener {
            val intent = Intent(root.context, Drive_save_activity::class.java)
            //TODO change list to noteList
//            intent.putExtra("list", list)
            startActivity(intent)
        }

        return root
    }

    lateinit var searchText:String

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
                        val adapter = NoteAdapter(noteList, db)
                        recyclerView.adapter = adapter

                        Thread(Runnable {
                            db.noteDao().insertNote(tempNote)
                        }).start()
                        noteCount++

                    }
                }
            }
            if(requestCode==2){
                // 타임라인 화면에서 달력 버튼 클릭 시 달력 액티비티 Intent.
                // 달력에서 날짜 선택 시 액티비티 종료
                // 동시에 년, 월, 일 정보를 전달.
                // 선택된 날짜를 사용하여 리스트를 변경
                // 리스트 변경 방법
                // 1. 리스트의 배열을 만들어서 가져옴 -> 메모리 사용이 클 것으로 예상됨
                // 2. 해당 날짜의 리스트를 데이터베이스에서 다시 가져옴 -> 날짜별로 데이터베이스에 리스트를 저장해야함.
                if(data != null){
                    selected_year = data.getStringExtra("year").toString()
                    selected_Month = data.getStringExtra("month").toString()
                    selected_dayOfMonth = data.getStringExtra("dayOfMonth").toString()
                    selected_Time.text = getString(R.string.year_month_day, selected_year, selected_Month, selected_dayOfMonth)
                    selected_Time_DB = selected_year.toInt()*10000+selected_Month.toInt()*100+selected_dayOfMonth.toInt()


                    noteList.clear()

                    Thread(Runnable {
                        Log.d("load", "db loading")
                        noteCount = db.noteDao().countNote2(selected_Time_DB)
                        val tempNoteList = db.noteDao().getAll2(selected_Time_DB)
                        for (i in 0 until noteCount) {
                            noteList.add(tempNoteList[i])
                            Log.d("Tag", tempNoteList[i].content!!)
                        }
                    }).start()

                    val adapter = NoteAdapter(noteList, db)
                    recyclerView.adapter = adapter


//                    refreshFragment(this, childFragmentManager)

                }
            }
            if(requestCode==3){
                // 검색 인텐트 종료 후 수행됨.
                // 검색어 추출
                if(data != null) {
                    searchText = data.getStringExtra("searching").toString()
//                    calendarButton.text = searchText
                    // 아직 미구현
                }
            }
        }
    }

//
//    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
//        var ft: FragmentTransaction = fragmentManager.beginTransaction()
//        ft.detach(fragment).attach(fragment).commit()
//    }
}