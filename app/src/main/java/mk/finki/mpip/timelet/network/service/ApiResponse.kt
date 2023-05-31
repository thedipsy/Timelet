package mk.finki.mpip.timelet.network.service

sealed interface ApiResponse<out T> {

  object Loading : ApiResponse<Nothing>

  data class Error<T>(val errorMessage: String? = null) : ApiResponse<T>

  data class Complete<T>(val value: T) : ApiResponse<T>

}