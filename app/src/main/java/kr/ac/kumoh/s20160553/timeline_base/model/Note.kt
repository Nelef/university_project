package kr.ac.kumoh.s20160553.timeline_base.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note (
    @PrimaryKey val uid: Int?,
    @ColumnInfo(name = "content") val content: String?
    //, @ColumnInfo(name = "contentTime") val contentTime: String?
)