package kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
        @PrimaryKey val uid: Int?,
        @ColumnInfo(name = "image_b") val image_b: Boolean,
        @ColumnInfo(name = "content") val content: String?,
        @ColumnInfo(name = "ymd") val ymd: Int,
        @ColumnInfo(name = "time") val time: Long,
        @ColumnInfo(name = "image") val image: String?
)
