package mk.finki.mpip.timelet.viewmodels.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mk.finki.mpip.timelet.domain.LoginUseCase
import mk.finki.mpip.timelet.network.service.ApiResponse

class LoginViewModel : ViewModel() {

  private val loginUseCase = LoginUseCase()
  private val _data: MutableLiveData<LoginViewState> = MutableLiveData(LoginViewState.Empty)
  val data: LiveData<LoginViewState>
    get() = _data

  fun login(username: String, password: String) {
    viewModelScope.launch(Dispatchers.IO) {
      when (val response = loginUseCase(username, password)) {
        is ApiResponse.Complete -> _data.postValue(LoginViewState.Success(response.value))
        is ApiResponse.Error -> _data.postValue(LoginViewState.Error(response.errorMessage))
        is ApiResponse.Loading -> _data.postValue(LoginViewState.Loading)
      }
    }
  }
}

sealed interface LoginViewState {
  object Loading : LoginViewState

  data class Error(val errorMessage: String?) : LoginViewState

  data class Success(val token: String) : LoginViewState

  object Empty: LoginViewState
}