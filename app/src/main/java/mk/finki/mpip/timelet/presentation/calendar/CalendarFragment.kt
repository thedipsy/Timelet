package mk.finki.mpip.timelet.presentation.calendar

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.prolificinteractive.materialcalendarview.CalendarDay
import mk.finki.mpip.timelet.R
import mk.finki.mpip.timelet.databinding.FragmentCalendarBinding
import mk.finki.mpip.timelet.network.NetworkUtil
import mk.finki.mpip.timelet.network.models.toInt
import mk.finki.mpip.timelet.presentation.MainActivity
import mk.finki.mpip.timelet.presentation.taskdetails.TaskDetailsActivity
import mk.finki.mpip.timelet.viewmodels.home.CalendarDayDecorator
import mk.finki.mpip.timelet.viewmodels.home.CalendarTask
import mk.finki.mpip.timelet.viewmodels.home.CalendarViewModel
import mk.finki.mpip.timelet.viewmodels.home.CalendarViewState
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class CalendarFragment : Fragment(), TasksRecyclerViewAdapter.OnTaskClickListener {

  companion object {
    fun createInstance() = CalendarFragment()
  }

  private var binding: FragmentCalendarBinding? = null
  private var viewModel: CalendarViewModel? = null
  private var adapter: TasksRecyclerViewAdapter? = null

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentCalendarBinding.inflate(layoutInflater)
    return binding?.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUp()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding = null
  }

  private fun setUp() {
    setUpViewModel()
    setUpViews()
    setUpListeners()
  }

  private fun setUpViewModel() {
    viewModel = ViewModelProvider(this)[CalendarViewModel::class.java]
    viewModel?.data?.observe(viewLifecycleOwner) { data ->
      updateUi(data)
    }
    viewModel?.selectedDateTasks?.observe(viewLifecycleOwner) { selectedDateTasks ->
      if (viewModel?.isInitialized == true) {
        binding?.contentLayout?.tvNoTasks?.isVisible = selectedDateTasks.isEmpty()
      }
      adapter?.updateList(selectedDateTasks)
    }
    val token = (requireActivity() as MainActivity).token
    viewModel?.initialize(token)
  }

  private fun setUpViews() {
    adapter = TasksRecyclerViewAdapter(requireContext())
    adapter?.setOnTaskClickListener(this)
    binding?.apply {
      contentLayout.rvTasks.layoutManager =
        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
      contentLayout.rvTasks.adapter = adapter
      toolbar.searchInputLayout.isVisible = true
    }
  }

  private fun setUpListeners() {
    binding?.apply {
      toolbar.apply {
        searchInputLayout.setOnClickListener {
          showDateTimePicker()
        }
        ivClearSearchBar.setOnClickListener {
          searchEditText.text = getString(R.string.search)
          val token = (requireActivity() as MainActivity).token
          viewModel?.initialize(token)
        }
      }

      pullToRefresh.setOnRefreshListener {
        getTasks()
        pullToRefresh.isRefreshing = false
      }
      noNetworkLayout.btnRetry.setOnClickListener {
        getTasks()
      }
      errorLayout.btnRetry.setOnClickListener {
        getTasks()
      }
    }
  }

  private fun showDateTimePicker() {
    val currentDateTime = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
      requireContext(),
      { _, year, month, dayOfMonth ->
        currentDateTime.set(Calendar.YEAR, year)
        currentDateTime.set(Calendar.MONTH, month)
        currentDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val localDate = LocalDate.of(
          currentDateTime.get(Calendar.YEAR),
          currentDateTime.get(Calendar.MONTH) + 1,
          currentDateTime.get(Calendar.DAY_OF_MONTH)
        )
        binding?.toolbar?.searchEditText?.text = localDate.toString()
        val token = (requireActivity() as MainActivity).token
        viewModel?.search(token, localDate.toString())
      },
      currentDateTime.get(Calendar.YEAR),
      currentDateTime.get(Calendar.MONTH),
      currentDateTime.get(Calendar.DAY_OF_MONTH)
    )

    datePickerDialog.show()
  }

  private fun getTasks() {
    if (NetworkUtil.isOnline(requireContext())) {
      val token = (requireActivity() as MainActivity).token
      viewModel?.initialize(token)
    } else {
      showNoConnectivityScreen()
    }
  }

  private fun updateUi(data: CalendarViewState) {
    when (data) {
      is CalendarViewState.Error -> showErrorScreen()
      is CalendarViewState.Loading -> showLoadingScreen()
      is CalendarViewState.CalendarContent -> showContentScreen(data)
      is CalendarViewState.SearchContent -> showSearchContentScreen(data)
    }
  }

  private fun showSearchContentScreen(data: CalendarViewState.SearchContent) {
    binding?.apply {
      contentLayout.root.isVisible = true
      contentLayout.calendarView.isVisible = false
      loadingLayout.root.isVisible = false
      errorLayout.root.isVisible = false
      noNetworkLayout.root.isVisible = false

      contentLayout.tvNoTasks.isVisible = data.tasks.isEmpty()
      adapter?.updateList(data.tasks)
    }
  }

  private fun showContentScreen(calendarData: CalendarViewState.CalendarContent) {
    binding?.apply {
      contentLayout.root.isVisible = true
      contentLayout.calendarView.isVisible = true
      loadingLayout.root.isVisible = false
      errorLayout.root.isVisible = false
      noNetworkLayout.root.isVisible = false

      contentLayout.calendarView.apply {
        removeDecorators()

        calendarData.tasks.forEach { task ->
          val date = Date.from(task.dueDate.atZone(ZoneId.systemDefault()).toInstant())
          val calDay = CalendarDay.from(date)
          val calDecorator = requireContext().getDrawable(R.drawable.cal_decorator)
          addDecorator(CalendarDayDecorator(calDecorator, calDay))
        }
        invalidateDecorators()

        setOnDateChangedListener { _, date, selected ->
          if (selected) {
            viewModel?.getSelectedDateTasks(date)
          }
        }
      }
    } ?: showErrorScreen()
  }

  private fun showLoadingScreen() {
    binding?.apply {
      loadingLayout.root.isVisible = true
      contentLayout.root.isVisible = false
      errorLayout.root.isVisible = false
      noNetworkLayout.root.isVisible = false
    }
  }

  private fun showErrorScreen() {
    binding?.apply {
      errorLayout.root.isVisible = true
      loadingLayout.root.isVisible = false
      contentLayout.root.isVisible = false
      noNetworkLayout.root.isVisible = false
    }
  }

  private fun showNoConnectivityScreen() {
    binding?.apply {
      noNetworkLayout.root.isVisible = true
      errorLayout.root.isVisible = false
      loadingLayout.root.isVisible = false
      contentLayout.root.isVisible = false
    }
  }

  override fun onTaskClick(task: CalendarTask) {
    AlertDialog.Builder(context)
      .setTitle("Task Details")
      .setMessage(
        "Title: ${task.title}\nDescription: ${task.description}\nDue Date: ${
          task.dueDate.toString().let { it.take(10) + " " + it.substring(11,16) }
        }"
      )
      .setNegativeButton("EDIT") { dialog, _ ->
        val intent = Intent(requireContext(), TaskDetailsActivity::class.java)
        intent.putExtra("id", task.id)
        intent.putExtra("title", task.title)
        intent.putExtra("description", task.description)
        intent.putExtra("dueDate", task.dueDate.toString())
        intent.putExtra("category", task.category.toInt())
        startActivity(intent)
        dialog.dismiss()
      }.setPositiveButton("OK") { dialog, _ ->
        dialog.dismiss()
      }
      .create()
      .show()
  }

  override fun onResume() {
    super.onResume()
    val token = (requireActivity() as MainActivity).token
    viewModel?.initialize(token)
    adapter?.updateList(listOf())
  }
}