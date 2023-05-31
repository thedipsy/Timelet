package mk.finki.mpip.timelet.viewmodels.taskdetails

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mk.finki.mpip.timelet.R
import mk.finki.mpip.timelet.domain.DeleteTaskUseCase
import mk.finki.mpip.timelet.domain.UpdateTaskUseCase
import mk.finki.mpip.timelet.network.models.Task

class TaskDetailsViewModel : ViewModel() {
  private val updateTaskUseCase = UpdateTaskUseCase()
  private val deleteTaskUseCase = DeleteTaskUseCase()

  private val _state: MutableLiveData<TaskDetailsViewState> =
    MutableLiveData(TaskDetailsViewState.Input)
  val state: LiveData<TaskDetailsViewState>
    get() = _state

  fun updateTask(token: String?, task: Task) {
    token?.let {
      viewModelScope.launch(Dispatchers.IO) {
        val result = updateTaskUseCase(token, task)
        if (result) {
          _state.postValue(TaskDetailsViewState.Success(R.string.successfull_update))
        } else {
          _state.postValue(TaskDetailsViewState.Error(R.string.failed_update))
        }
      }
    } ?: run {
      _state.value = TaskDetailsViewState.Error(R.string.global_error_message)
    }
  }

  fun deleteTask(token: String?, id: Int) {
    token?.let {
      viewModelScope.launch(Dispatchers.IO) {
        val result = deleteTaskUseCase(token, id)
        if (result) {
          _state.postValue(TaskDetailsViewState.Success(R.string.successfull_delete))
        } else {
          _state.postValue(TaskDetailsViewState.Error(R.string.failed_delete))
        }
      }
    } ?: run {
      _state.value = TaskDetailsViewState.Error(R.string.global_error_message)
    }
  }
}

sealed interface TaskDetailsViewState {
  object Input : TaskDetailsViewState
  data class Error(@StringRes val textRes: Int) : TaskDetailsViewState
  data class Success(@StringRes val textRes: Int) : TaskDetailsViewState
}