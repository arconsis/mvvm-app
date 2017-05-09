package com.arconsis.mvvmnotesample.notes

import com.arconsis.mvvmnotesample.data.*
import com.arconsis.mvvmnotesample.db.NoteDb
import com.arconsis.mvvmnotesample.util.NetworkChecker
import com.arconsis.mvvmnotesample.util.retrofit
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.droitateddb.EntityService
import retrofit2.Response

class NoteService(private val noteEntityService: EntityService<NoteDb>, private val networkChecker: NetworkChecker) {

    private val noteApi = retrofit().create(NoteApi::class.java)

    fun getNotesForUser(user: User): Observable<List<NoteDto>> {
        if (networkChecker.isNetworkAvailable()) {
            return Observable.fromCallable { noteApi.getNotesByUserId(user.id).execute() }
                    .subscribeOn(Schedulers.io()).map(this::handleNotesResponse)
                    .observeOn(AndroidSchedulers.mainThread())
        } else {
            return Observable.fromCallable { readNotesFromLocalDatabase() }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

    fun createNote(title: String, message: String, user: User): Observable<Result<NoteDto>> {
        if (networkChecker.isNetworkAvailable()) {
            return Observable.fromCallable { noteApi.createNote(title, message, user.id).execute() }
                    .subscribeOn(Schedulers.io())
                    .map(this::handleCreateResponse)
                    .observeOn(AndroidSchedulers.mainThread())
        } else {
            return Observable.just(Result.failure())
        }
    }

    fun clearNotes() {
        noteEntityService.get().forEach { note -> noteEntityService.delete(note) }

    }

    private fun handleNotesResponse(r: Response<NotesResponse>): List<NoteDto> {
        return if (r.isSuccessful) {
            updateLocalDatabase(r.body().notes)
        } else {
            readNotesFromLocalDatabase()
        }
    }

    private fun readNotesFromLocalDatabase(): List<NoteDto> {
        val db = noteEntityService.get()
        return db.map { (id, title, message, userId) ->
            NoteDto(id ?: -1, title, message, userId)
        }
    }

    private fun updateLocalDatabase(notes: List<NoteDto>): List<NoteDto> {
        noteEntityService.get().forEach { note -> noteEntityService.delete(note) }
        val db = notes.map { note -> convert(note) }
        noteEntityService.save(db)
        return notes
    }

    private fun handleCreateResponse(r: Response<NoteDto>): Result<NoteDto> {
        return if (r.isSuccessful) {
            val noteDto = r.body()
            noteEntityService.save(convert(noteDto))
            Result.success(noteDto)
        } else {
            Result.failure()
        }
    }

    private fun convert(note: NoteDto) = NoteDb(note.id, note.title, note.message, note.userId)
}

