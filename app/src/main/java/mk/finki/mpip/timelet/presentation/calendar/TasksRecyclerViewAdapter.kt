package mk.finki.mpip.timelet.presentation.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import mk.finki.mpip.timelet.R
import mk.finki.mpip.timelet.viewmodels.home.CalendarTask

class TasksRecyclerViewAdapter(val context: Context) :
  RecyclerView.Adapter<TasksRecyclerViewAdapter.TaskViewHolder>() {

  private var taskClickListener: OnTaskClickListener? = null
  private val allTasks = ArrayList<CalendarTask>()

  fun setOnTaskClickListener(listener: OnTaskClickListener) {
    taskClickListener = listener
  }

  fun updateList(newList: List<CalendarTask>) {
    allTasks.clear()
    allTasks.addAll(newList)
    notifyDataSetChanged()
  }

  override fun getItemCount() = allTasks.size

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
    val context = parent.context
    val inflater = LayoutInflater.from(context)
    val view = inflater.inflate(R.layout.item_task, parent, false)

    return TaskViewHolder(view)
  }

  override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
    val task = allTasks[position]
    holder.index.text = String.format("%s.", position + 1)
    holder.title.text = task.title
    holder.dueDate.text = task.dueDate.toString().let { it.take(10) + " " + it.substring(11, 16) }
    holder.view.setOnClickListener { taskClickListener?.onTaskClick(task) }
    if (task.category != null && task.category.iconRes != -1) {
      holder.icon.setImageDrawable(ContextCompat.getDrawable(context, task.category.iconRes))
    } else {
      holder.icon.setImageDrawable(null)
    }
  }

  inner class TaskViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    var view: View = v
    var title: TextView = view.findViewById(R.id.tv_title)
    var dueDate: TextView = view.findViewById(R.id.tv_due_date)
    var index: TextView = view.findViewById(R.id.tv_index)
    var icon: ImageView = view.findViewById(R.id.icon)
  }


  interface OnTaskClickListener {
    fun onTaskClick(task: CalendarTask)
  }
}

