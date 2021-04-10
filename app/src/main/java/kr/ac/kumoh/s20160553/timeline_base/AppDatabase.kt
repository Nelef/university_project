package kr.ac.kumoh.s20160553.timeline_base

import androidx.room.Database
import androidx.room.RoomDatabase
import kr.ac.kumoh.s20160553.timeline_base.dao.NoteDao
import kr.ac.kumoh.s20160553.timeline_base.model.Note

@Database(entities = [Note::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun noteDao(): NoteDao
}