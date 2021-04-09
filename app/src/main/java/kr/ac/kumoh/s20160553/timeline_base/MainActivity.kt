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
    private var listTextView = mutableListOf<TextView>()
    private lateinit var linearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val notePreferences = getSharedPreferences("myNote", Context.MODE_PRIVATE)
        noteCount = notePreferences.getInt("count", 0)

        for( i in 0 until noteCount){
            if (i % 2 == 0){
                linearLayout = LinearLayout(this)
                noteLayout.addView(linearLayout)
            }

            val text = notePreferences.getString("note$i", "fuck!").toString()
            addNote(text)
        }

        addButton.setOnClickListener {
            val text = noteEditText.text.toString()
            if (text == "")
                return@setOnClickListener

            if (noteCount % 2 == 0){
                linearLayout = LinearLayout(this)
                noteLayout.addView(linearLayout)
            }

            addNote(text)

            notePreferences.edit(true){
                putInt("count", ++noteCount)
                putString("note${noteCount - 1}", text)
            }
        }
    }

    private fun addNote(text: String){

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