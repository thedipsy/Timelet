package mk.finki.mpip.timelet.domain

import mk.finki.mpip.timelet.network.models.TasksResponse
import mk.finki.mpip.timelet.network.service.ApiResponse
import mk.finki.mpip.timelet.network.service.RetrofitService
import mk.finki.mpip.timelet.network.service.TimeletApi

class GetTasksUseCase {

  private val api: TimeletApi = RetrofitService.api()

  suspend operator fun invoke(token: String): ApiResponse<TasksResponse> {
    val response = api.getTasks(token)

    return if (response.isSuccessful) {
      response.body()?.let { tasksResponse ->
        ApiResponse.Complete(tasksResponse)
      } ?: ApiResponse.Error()
    } else {
      ApiResponse.Error()
    }
  }
}