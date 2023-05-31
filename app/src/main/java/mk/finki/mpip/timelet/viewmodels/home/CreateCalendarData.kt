package mk.finki.mpip.timelet.viewmodels.home

import mk.finki.mpip.timelet.network.models.Category
import mk.finki.mpip.timelet.network.models.TasksResponse
import mk.finki.mpip.timelet.network.models.toCategory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CreateCalendarData {

  operator fun invoke(tasksResponse: TasksResponse): CalendarViewState.CalendarContent {
    val tasks = tasksResponse.value.items.map { task ->
      CalendarTask(
        task.id,
        task.title,
        task.description ?: "",
        convertStringToLocalDateTime(task.dueDate),
        task.category?.toCategory()
      )
    }
    return CalendarViewState.CalendarContent(tasks)
  }

  private fun convertStringToLocalDateTime(dateTimeString: String): LocalDateTime {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    return LocalDateTime.parse(dateTimeString, formatter)
  }
}

data class CalendarTask(
  val id: Int?,
  val title: String,
  val description: String,
  val dueDate: LocalDateTime,
  val category: Category?
)