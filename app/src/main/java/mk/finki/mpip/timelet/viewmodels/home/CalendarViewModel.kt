package mk.finki.mpip.timelet.viewmodels.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mk.finki.mpip.timelet.domain.GetTasksUseCase
import mk.finki.mpip.timelet.domain.SearchUseCase
import mk.finki.mpip.timelet.network.service.ApiResponse
import java.time.ZoneId
import java.util.*

class CalendarViewModel : ViewModel() {

  private val getTasksUseCase = GetTasksUseCase()
  private val searchUseCase = SearchUseCase()
  private val createCalendarData = CreateCalendarData()
  private val createSearchContentData = CreateSearchContentData()
  private val _data: MutableLiveData<CalendarViewState> = MutableLiveData(CalendarViewState.Loading)
  val data: LiveData<CalendarViewState>
    get() = _data

  private val _selectedDateTasks: MutableLiveData<List<CalendarTask>> = MutableLiveData(listOf())
  val selectedDateTasks: MutableLiveData<List<CalendarTask>>
    get() = _selectedDateTasks

  var isInitialized = false
    private set

  fun initialize(token: String?) {
    token ?: return
    viewModelScope.launch(Dispatchers.IO) {
      when (val response = getTasksUseCase(token)) {
        is ApiResponse.Complete -> _data.postValue(createCalendarData(response.value))
        is ApiResponse.Error -> _data.postValue(CalendarViewState.Error)
        is ApiResponse.Loading -> _data.postValue(CalendarViewState.Loading)
      }
      isInitialized = true
    }
  }

  fun getSelectedDateTasks(selectedCalendarDay: CalendarDay) {
    val allTasks = (_data.value as? CalendarViewState.CalendarContent)?.tasks ?: listOf()

    val selectedDateTasks = allTasks.filter { task ->
      val date = Date.from(task.dueDate.atZone(ZoneId.systemDefault()).toInstant())
      val calDay = CalendarDay.from(date)
      calDay == selectedCalendarDay
    }
    _selectedDateTasks.value = selectedDateTasks
  }

  fun search(token: String?, search: String) {
    token?.let {
      viewModelScope.launch(Dispatchers.IO) {
        when (val response = searchUseCase(token, search)) {
          is ApiResponse.Complete -> _data.postValue(createSearchContentData(response.value))
          is ApiResponse.Error -> _data.postValue(CalendarViewState.Error)
          is ApiResponse.Loading -> _data.postValue(CalendarViewState.Loading)
        }
      }
    } ?: CalendarViewState.Error
  }
}

sealed interface CalendarViewState {
  object Loading : CalendarViewState

  object Error : CalendarViewState

  data class CalendarContent(val tasks: List<CalendarTask>) : CalendarViewState

  data class SearchContent(val tasks: List<CalendarTask>) : CalendarViewState
}