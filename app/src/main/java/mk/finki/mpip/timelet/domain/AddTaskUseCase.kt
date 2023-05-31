package mk.finki.mpip.timelet.domain

import mk.finki.mpip.timelet.network.models.Task
import mk.finki.mpip.timelet.network.service.RetrofitService
import mk.finki.mpip.timelet.network.service.TimeletApi

class AddTaskUseCase {

  private val api: TimeletApi = RetrofitService.api()

  suspend operator fun invoke(token: String, task: Task): Boolean {
    val response = api.saveTask(token, task)

    return if (response.isSuccessful) {
      response.body()?.let { _ ->
        true
      } ?: false
    } else {
      false
    }
  }
}