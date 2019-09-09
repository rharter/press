package me.saket.compose.shared.editor

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.subscribe
import com.badoo.reaktive.completable.subscribeOn
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.firstOrError
import com.badoo.reaktive.observable.observableOfEmpty
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.single.flatMapCompletable
import com.benasher44.uuid.Uuid
import me.saket.compose.shared.Presenter
import me.saket.compose.shared.note.NoteRepository

class EditorPresenter(
  private val noteUuid: Uuid,
  private val noteRepository: NoteRepository,
  private val ioScheduler: Scheduler
) : Presenter<EditorEvent, EditorUiModel> {

  override fun contentModels(events: Observable<EditorEvent>): Observable<EditorUiModel> {
    return observableOfEmpty()
  }

  fun saveEditorContentOnExit(content: CharSequence) {
    createUpdateOrDeleteNote(content)
        .subscribeOn(ioScheduler)
        .subscribe()
  }

  private fun createUpdateOrDeleteNote(content: CharSequence): Completable {
    return noteRepository.note(noteUuid)
        .firstOrError()
        .flatMapCompletable { (existingNote) ->
          val hasExistingNote = existingNote != null
          val shouldDelete = hasExistingNote && content.isBlank()

          if (shouldDelete) {
            noteRepository.markAsDeleted(noteUuid)
          } else {
            if (hasExistingNote) {
              noteRepository.update(noteUuid, content.toString())
            }
            else {
              noteRepository.create(noteUuid, content.toString())
            }
          }
        }
  }

  interface Factory {
    fun create(noteUuid: Uuid): EditorPresenter
  }
}