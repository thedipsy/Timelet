package mk.finki.mpip.timelet.network.models

import java.io.Serializable
import java.time.LocalDateTime

data class TasksResponse(
  val value: Tasks
) : Serializable
