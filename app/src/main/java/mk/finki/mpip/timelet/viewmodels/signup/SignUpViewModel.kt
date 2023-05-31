package mk.finki.mpip.timelet.viewmodels.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mk.finki.mpip.timelet.domain.SignUpUseCase
import mk.finki.mpip.timelet.network.service.ApiResponse

class SignUpViewModel : ViewModel() {

  private val signUpUseCase = SignUpUseCase()
  private val _data: MutableLiveData<SignUpViewState> = MutableLiveData(SignUpViewState.Empty)
  val data: LiveData<SignUpViewState>
    get() = _data

  fun signUp(username: String, password: String, confirmPassword: String) {
    viewModelScope.launch {
      when (val response = signUpUseCase(username, password, confirmPassword)) {
        is ApiResponse.Complete -> _data.postValue(SignUpViewState.Success(response.value))
        is ApiResponse.Error -> _data.postValue(SignUpViewState.Error(response.errorMessage))
        is ApiResponse.Loading -> _data.postValue(SignUpViewState.Loading)
      }
    }
  }
}

sealed interface SignUpViewState {
  object Loading : SignUpViewState

  data class Error(val errorMessage: String?) : SignUpViewState

  data class Success(val token: String) : SignUpViewState

  object Empty: SignUpViewState
}