package kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.ExifInterface
import android.net.Uri
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

    lateinit var root2: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_timeline, container, false)

        root2 = root.context

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
            "noteDBa4d5aaaaaa"
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

            var tempNote = Note(null, false, text, SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(System.currentTimeMillis()).toInt(), System.currentTimeMillis(), "",0.0, 0.0)

            // DB에 메모 추가
            Thread(Runnable {
                var tid = db.noteDao().insertNote(tempNote).toInt()
                tempNote = Note(tid, false, text, selected_Time_DB, System.currentTimeMillis(), "", 0.0, 0.0)
                noteList.add(tempNote)
                noteCount++
            }).start()

            val adapter = NoteAdapter(noteList, db, this)
            recyclerView.adapter = adapter

            noteList.sortBy { it.time }


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

        val adapter = NoteAdapter(noteList, db, this)
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
            refreshOpreation()
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
                        noteCount = db.noteDao().countNoteSelectedTime(selected_Time_DB)
                        val tempNoteList = db.noteDao().getNoteListSelectedTime(selected_Time_DB)
                        for (i in 0 until noteCount) {
                            noteList.add(tempNoteList[i])
                        }
                    }).start()

                    val adapter = NoteAdapter(noteList, db, this)
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

                    val adapter = NoteAdapter(noteList, db, this)
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

                    // ------------------ 지도 코드 ---------------------

                    var LATITUDE = exif?.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
                    var LONGITUDE = exif?.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)

                    var lat_result = 0.0
                    var lng_result = 0.0

                    if(LATITUDE != null && LONGITUDE != null) {
                        var latArray = LATITUDE.split(",", "/")
                        var lngArray = LONGITUDE.split(",", "/")
                        lat_result = latArray[0].toDouble()/latArray[1].toDouble() + latArray[2].toDouble()/latArray[3].toDouble() / 60 + latArray[4].toDouble()/latArray[5].toDouble() / 3600
                        lng_result = lngArray[0].toDouble()/lngArray[1].toDouble() + lngArray[2].toDouble()/lngArray[3].toDouble() / 60 + lngArray[4].toDouble()/lngArray[5].toDouble() / 3600
                    }

                    if (temp == null || LATITUDE == null || LONGITUDE == null){
                        tempNote = Note(null, false, "",  SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(System.currentTimeMillis()).toInt(), System.currentTimeMillis(), ImageUri.toString(), 0.0, 0.0)
                    } else {
                        var date: Date = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault()).parse(temp)
                        tempNote = Note(null, false, "",  SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date).toInt(), date.time, ImageUri.toString(), lat_result, lng_result)
                    }
                    // ------------------ 지도 코드 ---------------------
                    // 데이터베이스 버전 바꾸고(dbname) -> TimelineFragment.kt, NotificationReceiver.kt
                    // model/Note에
                    //    @ColumnInfo(name = "LATITUDE") val LATITUDE: Double?,
                    //    @ColumnInfo(name = "LONGITUDE") val LONGITUDE: Double?
                    // 추가.
                    // MainActivity.kt
                    //        <activity
                    //            android:name=".ui.map.LocationTrackingActivity"
                    //            android:theme="@style/Theme.DialogStyle" />
                    // 로 변경

                    Thread(Runnable {
                        var tid = db.noteDao().insertNote(tempNote).toInt()
                        tempNote = Note(tid, tempNote.image_b, tempNote.content, tempNote.ymd, tempNote.time, tempNote.image, tempNote.LATITUDE, tempNote.LONGITUDE)
                        noteList.add(tempNote)
                        noteCount++
                    }).start()

                    val adapter = NoteAdapter(noteList, db, this)
                    recyclerView.adapter = adapter
                }
                MainActivity.lock = false
            }
            if (requestCode == 2000) {
                if (data != null) {
                    Thread(Runnable {
                        var uid = data.getIntExtra("uid", 0)
                        var cNote:Note = db.noteDao().getNoteUsingUid(uid)
                        Log.d("uid2", cNote.uid.toString())
                        var tempNote = Note(cNote.uid,
                            cNote.image_b,
                            data.getStringExtra("content"),
                            data.getIntExtra("yyyyMMdd", 20200620),
                            data.getLongExtra("time", cNote.time),
                            cNote.image,
                            cNote.LATITUDE, cNote.LONGITUDE
                        )
                        Log.d("uid3", data.getLongExtra("time", cNote.time).toString())
                        db.noteDao().update(tempNote)
                    }).start()
                    val adapter = NoteAdapter(noteList, db, this)
                    recyclerView.adapter = adapter
                }
                MainActivity.lock = false
            }
        }
    }

    fun refreshOpreation(){
        noteList.clear()
        Thread(Runnable {
            noteCount = db.noteDao().countNoteSelectedTime(selected_Time_DB)
            val tempNoteList = db.noteDao().getNoteListSelectedTime(selected_Time_DB)
            for (i in 0 until noteCount) {
                noteList.add(tempNoteList[i])
            }
            noteList.sortBy { it.time }
        }).start()
        selected_Time.text = selected_Time_String
        val adapter = NoteAdapter(noteList, db, this)
        recyclerView.adapter = adapter
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