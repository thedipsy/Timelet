package mk.finki.mpip.timelet.network.models

import androidx.annotation.DrawableRes
import mk.finki.mpip.timelet.R
import java.io.Serializable

data class Task(
  val id: Int? = null,
  val title: String,
  val description: String?,
  val dueDate: String,
  val category: Int? = null
) : Serializable

enum class Category(val iconRes: Int) {
  WORK(R.drawable.ic_category_work),
  EDUCATION(R.drawable.ic_category_education),
  TRAVEL(R.drawable.ic_category_plane),
  FITNESS(R.drawable.ic_category_fitness),
  SHOPPING(R.drawable.ic_cateogry_shopping),
  NO_CATEGORY(-1)
}

fun Int.toCategory() =
  when (this) {
    0 -> Category.WORK
    1 -> Category.EDUCATION
    2 -> Category.TRAVEL
    3 -> Category.FITNESS
    4 -> Category.SHOPPING
    else -> null
  }

fun Category?.toInt() =
  when (this) {
    Category.WORK -> 0
    Category.EDUCATION -> 1
    Category.TRAVEL -> 2
    Category.FITNESS -> 3
    Category.SHOPPING -> 4
    else -> null
  }

data class Tasks(
  val items: List<Task>
)