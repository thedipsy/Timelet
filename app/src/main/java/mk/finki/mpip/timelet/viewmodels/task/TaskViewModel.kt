package mk.finki.mpip.timelet.viewmodels.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mk.finki.mpip.timelet.domain.AddTaskUseCase
import mk.finki.mpip.timelet.network.models.Task

class TaskViewModel : ViewModel() {

  private val addTaskUseCase = AddTaskUseCase()
  private val _state: MutableLiveData<TaskViewState> = MutableLiveData(TaskViewState.INPUT)
  val state: LiveData<TaskViewState>
    get() = _state

  fun addTask(token: String?, task: Task) {
    token?.let {
      viewModelScope.launch(Dispatchers.IO) {
        val result = addTaskUseCase(token, task)
        _state.postValue(if(result) TaskViewState.SUCCESS else TaskViewState.ERROR)
      }
    } ?: run {
      _state.value = TaskViewState.ERROR
    }
  }

  fun clearState() {
    _state.value = TaskViewState.INPUT
  }
}

enum class TaskViewState {
  INPUT,
  ERROR,
  SUCCESS
}
