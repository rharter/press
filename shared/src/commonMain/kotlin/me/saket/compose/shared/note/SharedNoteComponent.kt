package me.saket.compose.shared.note

import me.saket.compose.data.shared.Note
import me.saket.compose.shared.db.DateTimeAdapter
import me.saket.compose.shared.db.UuidAdapter
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal object SharedNoteComponent {
  val module = module {
    factory<NoteRepository> { RealNotesRepository(get(), get(named("io")), get()) }
    single {
      Note.Adapter(
          uuidAdapter = UuidAdapter(),
          createdAtAdapter = DateTimeAdapter(),
          updatedAtAdapter = DateTimeAdapter(),
          deletedAtAdapter = DateTimeAdapter()
      )
    }
  }
}