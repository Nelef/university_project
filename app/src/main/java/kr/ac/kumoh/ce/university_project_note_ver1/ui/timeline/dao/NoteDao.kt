package kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.model.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    fun getAll(): List<Note>

    @Query("SELECT * FROM note WHERE ymd=:ymd")
    fun getNoteListSelectedTime(ymd: Int): List<Note>

    @Query("SELECT COUNT(*) FROM note")
    fun countNote(): Int

    @Query("SELECT COUNT(*) FROM note WHERE ymd=:ymd")
    fun countNoteSelectedTime(ymd: Int): Int

    @Insert
    fun insertNote(note: Note)

    @Query("DELETE FROM note")
    fun deleteAll()

    @Delete
    fun delete(note: Note)

    @Query("SELECT * FROM note WHERE content LIKE :search")
    fun findByResult(search: String): List<Note>
}