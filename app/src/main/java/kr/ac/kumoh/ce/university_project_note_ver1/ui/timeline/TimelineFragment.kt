package kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kr.ac.kumoh.ce.university_project_note_ver1.MainActivity
import kr.ac.kumoh.ce.university_project_note_ver1.R
import kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.model.Note
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat

import java.util.*

class TimelineFragment : Fragment() {

    companion object{
        lateinit var db: AppDatabase                                // DB 변수
    }

    private var noteCount:Int = 0                               // DB에 저장된 노트의 개수
    private var noteList: MutableList<Note> = mutableListOf()   // DB에 저장된 노트의 리스트

    lateinit var noteEditText:EditText                          // 노트 입력칸
    lateinit var addButton:Button                               // 노트 입력 확인버튼
    lateinit var recyclerView:RecyclerView                      // 노트 리스트 RecyclerView
    lateinit var addImage:Button                                // 이미지 추가 버튼

    lateinit var calendarButton:Button                          // 날짜 설정 액티비티 호출 버튼
    lateinit var selected_Time: TextView                        // 설정된 날자를 표시할 TextView
    lateinit var selected_year:String                           // 설정된 날짜의 해
    lateinit var selected_Month:String                          // 설정된 날짜의 월
    lateinit var selected_dayOfMonth:String                     // 설정된 날짜의 일

    var selected_Time_DB:Int = 0                                // 설정된 날짜를 Int형으로 저장
    var selected_Time_String:String = ""                        // 설정된 날짜를 String형으로 저장

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
        addImage = root.findViewById(R.id.button_add_image)

        // 오늘 날짜 가져오기
        val date: Date = Calendar.getInstance().time
        selected_year = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
        selected_Month = SimpleDateFormat("MM", Locale.getDefault()).format(date)
        selected_dayOfMonth = SimpleDateFormat("dd", Locale.getDefault()).format(date)
        selected_Time_String = getString(R.string.year_month_day, selected_year, selected_Month, selected_dayOfMonth)
        selected_Time.text = selected_Time_String
        selected_Time_DB = selected_year.toInt()*10000+selected_Month.toInt()*100+selected_dayOfMonth.toInt()

        // DB 초기화
        db = Room.databaseBuilder(
            root.context,
            AppDatabase::class.java,
            "noteDBa4d5aaa"
        ).build()

        // DB에서 내용 불러오기
        Thread(Runnable {
            Log.d("load", "db loading")
            noteCount = db.noteDao().countNoteSelectedTime(selected_Time_DB)
            val tempNoteList = db.noteDao().getNoteListSelectedTime(selected_Time_DB)
            for (i in 0 until noteCount) {
                noteList.add(tempNoteList[i])
                Log.d("Tag", tempNoteList[i].content!!)
            }
        }).start()

        noteList.sortByDescending { it.ymd }            // 날짜를 기준으로 리스트 정렬

        // 노트 추가
        addButton.setOnClickListener{
            val text = noteEditText.text.toString()

            // 입력칸이 빈 경우
            if(text == "") return@setOnClickListener

            val tempNote = Note(null, false, text, selected_Time_DB, System.currentTimeMillis(), "")
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
            val intent = Intent(root.context, TimelineCalendarActivity::class.java)
            startActivityForResult(intent, 2)
        }

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        recyclerView = root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(root.context)

        val adapter = NoteAdapter(noteList, db)
        recyclerView.adapter = adapter

        val button_search_memo:Button = root.findViewById(R.id.button_search_memo)
        button_search_memo.setOnClickListener {
            // 메모 검색 버튼 (실험중)2
            val intent = Intent(root.context, MemoSearchActivity::class.java)
            startActivityForResult(intent, 3)
        }

        //사진
        addImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
            startActivityForResult(intent, 4)
        }

        // 리사이클러뷰 위로 당겨서 새로고침 하는 기능
        // 단, 검색중에 수행하면 오늘 날짜로 돌아옴
        val refresh_layout = root.findViewById<SwipeRefreshLayout>(R.id.refresh_layout)
        refresh_layout.setOnRefreshListener {
            noteList.clear()
            Thread(Runnable {
                Log.d("load", "db loading")
                noteCount = db.noteDao().countNoteSelectedTime(selected_Time_DB)
                val tempNoteList = db.noteDao().getNoteListSelectedTime(selected_Time_DB)
                for (i in 0 until noteCount) {
                    noteList.add(tempNoteList[i])
                    Log.d("Tag", tempNoteList[i].content!!)
                }
            }).start()
            selected_Time.text = selected_Time_String
            val adapter = NoteAdapter(noteList, db)
            recyclerView.adapter = adapter
            refresh_layout.isRefreshing = false
        }


        return root
    }

    lateinit var searchText:String

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==RESULT_OK){
            if(requestCode==2){
                if(data != null){
                    selected_year = data.getStringExtra("year").toString()
                    selected_Month = data.getStringExtra("month").toString()
                    selected_dayOfMonth = data.getStringExtra("dayOfMonth").toString()
                    selected_Time_String = getString(R.string.year_month_day, selected_year, selected_Month, selected_dayOfMonth)
                    selected_Time.text = selected_Time_String
                    selected_Time_DB = selected_year.toInt()*10000+selected_Month.toInt()*100+selected_dayOfMonth.toInt()
                    noteList.clear()

                    Thread(Runnable {
                        Log.d("load", "db loading")
                        noteCount = db.noteDao().countNoteSelectedTime(selected_Time_DB)
                        val tempNoteList = db.noteDao().getNoteListSelectedTime(selected_Time_DB)
                        for (i in 0 until noteCount) {
                            noteList.add(tempNoteList[i])
                            Log.d("Tag", tempNoteList[i].content!!)
                        }
                    }).start()

                    val adapter = NoteAdapter(noteList, db)
                    recyclerView.adapter = adapter

//                    noteEditText.isClickable = true
//                    noteEditText.isEnabled = true
                }
            }
            if(requestCode==3){
                // 검색 인텐트 종료 후 수행됨.
                // 검색어 추출
                if(data != null) {
                    searchText = data.getStringExtra("searchText").toString()
                    selected_Time.text = "${searchText} 검색결과"
//                    noteEditText.isClickable = false
//                    noteEditText.isEnabled = false

                    Thread(Runnable {
                        val tempNoteList = db.noteDao().findByResult("%$searchText%")
                        noteList.clear()
                        Log.d("$searchText 검색", tempNoteList.toString())
                        for (i in 0 until tempNoteList.size) {
                            noteList.add(tempNoteList[i])
                        }
                    }).start()

                    val adapter = NoteAdapter(noteList, db)
                    recyclerView.adapter = adapter
                }
            }
            if (requestCode==4){
                // 이미지 추가 인텐트 종료 후 수행됨.
                if(data != null){
                    val temp_uri = data.data
                    val ImageUri = Uri.parse(temp_uri.toString())
                    val absolutePath = getRealPathFromUri(ImageUri)
                    var exif: ExifInterface? = null
                    lateinit var tempNote: Note

                    try {
                        Log.d("ExifPath", absolutePath.toString())
                        exif = ExifInterface(File(absolutePath).toString())
                    }catch (e: IOException){
                        e.printStackTrace()
                    }

                    var temp = exif?.getAttribute(ExifInterface.TAG_DATETIME)
                    Log.d("exif_r", temp.toString())

                    if (temp == null){
                        tempNote = Note(null, false, "",  SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(System.currentTimeMillis()).toInt(), System.currentTimeMillis(), ImageUri.toString())
                    } else {
                        var date: Date = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault()).parse(temp)
                        tempNote = Note(null, false, "",  SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date).toInt(), date.time, ImageUri.toString())
                    }
                    
                    noteList.add(tempNote)
                    val adapter = NoteAdapter(noteList, db)
                    recyclerView.adapter = adapter

                    Thread(Runnable {
                        db.noteDao().insertNote(tempNote)
                    }).start()
                    noteCount++
                }
                MainActivity.lock = false
            }
        }
    }
    fun getRealPathFromUri(contentURI: Uri) : String?{
        val result: String?

        val cursor: Cursor = requireContext().contentResolver.query(contentURI, null, null, null, null)!!

        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }
}