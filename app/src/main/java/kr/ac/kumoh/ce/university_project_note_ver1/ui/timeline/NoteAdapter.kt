package kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.ac.kumoh.ce.university_project_note_ver1.R
import kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.model.Note

class NoteAdapter internal constructor(list: List<Note>) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    private var mData: List<Note>

    // 생성자에서 데이터 리스트 객체를 전달받음.
    init {
        mData = list
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView1: TextView
        var recordTime: TextView
        init {
            // 뷰 객체에 대한 참조. (hold strong reference)
            textView1 = itemView.findViewById(R.id.item_textView)
            recordTime = itemView.findViewById(R.id.recordTime)
        }
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.list_row_item, parent, false)
        return ViewHolder(view)
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cNote = mData[position]
        holder.textView1.text = cNote.content.toString()
        holder.recordTime.text = cNote.ymd.toString()
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    override fun getItemCount(): Int {
        return mData.size
    }
}