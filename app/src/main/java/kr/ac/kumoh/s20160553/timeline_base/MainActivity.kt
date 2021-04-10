package kr.ac.kumoh.s20160553.timeline_base

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.room.Room
import kr.ac.kumoh.s20160553.timeline_base.model.Note

class MainActivity : AppCompatActivity() {

    val noteLayout: LinearLayout by lazy {
        findViewById<LinearLayout>(R.id.noteLayout)
    }

    val addButton: Button by lazy {
        findViewById<Button>(R.id.addButton)
    }

    val noteEditText: EditText by lazy {
        findViewById<EditText>(R.id.noteEditText)
    }

    private var noteCount: Int = 0
    private lateinit var noteList: List<Note>
    private var listTextView = mutableListOf<TextView>()
    private lateinit var linearLayout: LinearLayout

    lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "noteDB"
        ).build()

        Thread(Runnable {
            noteCount = db.memoDao().countMemo()
            noteList = db.memoDao().getAll()
        })

        for( i in 0 until noteCount){
            addNote(i, noteList[i].content.toString())
        }

        addButton.setOnClickListener {
            val text = noteEditText.text.toString()
            if (text == "")
                return@setOnClickListener

            addNote(noteCount++, text)

            Thread(Runnable {
                db.memoDao().insertMemo(Note(null, text))
            })
        }
    }

    private fun addNote(i: Int, text: String){

        if (i % 2 == 0){
            linearLayout = LinearLayout(this)
            noteLayout.addView(linearLayout)
        }

        val noteTextView: TextView = TextView(this)

        val layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )
        layoutParams.setMargins(20)
        layoutParams.weight = 1F
        noteTextView.gravity = Gravity.CENTER
        noteTextView.setPadding(10)
        noteTextView.layoutParams = layoutParams
        noteTextView.setTextSize(25F)
        noteTextView.setText(text)
        noteTextView.layoutParams
        noteTextView.setBackgroundResource(R.drawable.note_background)
        listTextView.add(noteTextView)
        linearLayout.addView(noteTextView)
    }
}