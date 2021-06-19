package kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kr.ac.kumoh.ce.university_project_note_ver1.R
import kr.ac.kumoh.ce.university_project_note_ver1.ui.map.LocationTrackingActivity
import kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.model.Note
import java.text.SimpleDateFormat
import java.util.*

class NoteAdapter internal constructor(list: MutableList<Note>, database: AppDatabase) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    private var mData: MutableList<Note>
    private var db: AppDatabase
    lateinit var context2:Context
    // 생성자에서 데이터 리스트 객체를 전달받음.
    init {
        mData = list
        db = database
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView1: TextView
        var recordTime: TextView
        var itemImageView: ImageView
        var deleteButton: Button
        init {
            // 뷰 객체에 대한 참조. (hold strong reference)
            textView1 = itemView.findViewById(R.id.item_textView)
            recordTime = itemView.findViewById(R.id.recordTime)
            itemImageView = itemView.findViewById(R.id.item_ImageView)
            deleteButton = itemView.findViewById(R.id.item_button)
        }
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        context2 = context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.list_row_item, parent, false)
        return ViewHolder(view)
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cNote = mData[position]
        holder.textView1.text = cNote.content.toString()
        holder.recordTime.text = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(cNote.time)
        if(cNote.image!="") {
            holder.itemImageView.setImageURI(Uri.parse(cNote.image.toString()))
        }
        // 여기에 이미지뷰 내용 추가

        holder.deleteButton.setOnClickListener {
            mData.remove(cNote)
            Thread(Runnable {
                db.noteDao().delete(cNote)
            }).start()
            notifyDataSetChanged()      // 데이터 변경 시 갱신하는 코드
        }
        // ------------------ 지도 코드 ---------------------
        holder.itemImageView.setOnClickListener {
            var intent = Intent(context2, LocationTrackingActivity::class.java)
            intent.putExtra("LATITUDE", cNote.LATITUDE)
            intent.putExtra("LONGITUDE", cNote.LONGITUDE)
            startActivity(context2, intent, null)
        }
        // ------------------ 지도 코드 ---------------------
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    override fun getItemCount(): Int {
        return mData.size
    }
}