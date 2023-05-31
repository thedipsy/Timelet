package mk.finki.mpip.timelet.domain

import mk.finki.mpip.timelet.network.models.SignUpRequest
import mk.finki.mpip.timelet.network.service.ApiResponse
import mk.finki.mpip.timelet.network.service.RetrofitService
import mk.finki.mpip.timelet.network.service.TimeletApi

class SignUpUseCase {

  private val api: TimeletApi = RetrofitService.api()

  suspend operator fun invoke(username: String, password: String, confirmPassword: String): ApiResponse<String> {
    if (password != confirmPassword) return ApiResponse.Error("Passwords do not match")
    val signUpRequest = SignUpRequest(username, password)

    val response = api.register(signUpRequest)

    return if (response.isSuccessful) {
      response.body()?.let { signUpResponse ->
        if (signUpResponse.isSucess) {
          ApiResponse.Complete(signUpResponse.value)
        } else {
          ApiResponse.Error(signUpResponse.errorMessage)
        }
      } ?: ApiResponse.Error()
    } else {
      ApiResponse.Error()
    }
  }
}