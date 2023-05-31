package mk.finki.mpip.timelet.domain

import mk.finki.mpip.timelet.network.models.Task
import mk.finki.mpip.timelet.network.service.RetrofitService
import mk.finki.mpip.timelet.network.service.TimeletApi

class UpdateTaskUseCase {
  private val api: TimeletApi = RetrofitService.api()

  suspend operator fun invoke(token: String, task: Task): Boolean {
    task.id ?: return false
    val response = api.updateTask(token, task.id, task)

    return if (response.isSuccessful) {
      response.body()?.isSucess ?: false
    } else {
      false
    }
  }
}