package mk.finki.mpip.timelet.network.models

data class SearchRequest (
  val from: String,
  val to: String,
  val page: Int,
  val count: Int
  )