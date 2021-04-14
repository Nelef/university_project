package kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note (
    @PrimaryKey val uid: Int?,
    @ColumnInfo(name = "content") val content: String?
    //, @ColumnInfo(name = "contentTime") val contentTime: String?
)