package mk.finki.mpip.timelet.domain

import mk.finki.mpip.timelet.network.models.LoginRequest
import mk.finki.mpip.timelet.network.service.ApiResponse
import mk.finki.mpip.timelet.network.service.RetrofitService
import mk.finki.mpip.timelet.network.service.TimeletApi

class LoginUseCase {

  private val api: TimeletApi = RetrofitService.api()

  suspend operator fun invoke(username: String, password: String): ApiResponse<String> {
    val loginRequest = LoginRequest(username, password)

    val response = api.login(loginRequest)

    return if (response.isSuccessful) {
      response.body()?.let { loginResponse ->
        if (loginResponse.isSucess) {
          ApiResponse.Complete(loginResponse.value)
        } else {
          ApiResponse.Error(loginResponse.errorMessage)
        }
      } ?: ApiResponse.Error()
    } else {
      ApiResponse.Error()
    }
  }
}