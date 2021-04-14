package kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline

import androidx.room.Database
import androidx.room.RoomDatabase
import kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.dao.NoteDao
import kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.model.Note

@Database(entities = [Note::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun noteDao(): NoteDao
}