package kr.ac.kumoh.s20160553.timeline_base.dao

import androidx.room.*
import kr.ac.kumoh.s20160553.timeline_base.model.Note

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