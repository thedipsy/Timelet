package mk.finki.mpip.timelet.domain

import mk.finki.mpip.timelet.network.service.RetrofitService
import mk.finki.mpip.timelet.network.service.TimeletApi

class DeleteTaskUseCase {
  private val api: TimeletApi = RetrofitService.api()

  suspend operator fun invoke(token: String, id: Int): Boolean {
    val response = api.deleteTask(token, id)

    return if (response.isSuccessful) {
      response.body()?.isSucess ?: false
    } else {
      false
    }
  }
}