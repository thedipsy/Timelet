package mk.finki.mpip.timelet.network.models

data class BaseResponse(
  val value: String,
  val isSucess: Boolean,
  val errorMessage: String?
)