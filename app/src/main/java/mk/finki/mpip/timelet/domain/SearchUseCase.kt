package mk.finki.mpip.timelet.domain

import mk.finki.mpip.timelet.network.models.SearchRequest
import mk.finki.mpip.timelet.network.models.TasksResponse
import mk.finki.mpip.timelet.network.service.ApiResponse
import mk.finki.mpip.timelet.network.service.RetrofitService
import mk.finki.mpip.timelet.network.service.TimeletApi

class SearchUseCase {

  private val api: TimeletApi = RetrofitService.api()

  suspend operator fun invoke(token: String, search: String): ApiResponse<TasksResponse> {
    val searchRequest = SearchRequest(
      search + "T00:00:00",
      search + "T23:59:59",
      0,
      0
    )

    val response = api.search(token, searchRequest)

    return if (response.isSuccessful) {
      response.body()?.let { searchResponse ->
        ApiResponse.Complete(searchResponse)
      } ?: ApiResponse.Error()
    } else {
      ApiResponse.Error()
    }
  }
}