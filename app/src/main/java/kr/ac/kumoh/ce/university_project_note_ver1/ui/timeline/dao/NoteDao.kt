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

    @Query("SELECT COUNT(*) FROM note")
    fun countNote(): Int

    @Insert
    fun insertNote(note: Note)

    @Query("DELETE FROM note")
    fun deleteAll()

    @Delete
    fun delete(note: Note)

//    @Query("SELECT * FROM history WHERE result LIKE :result LIMIT 1")
//    fun findByResult(result: String)

}