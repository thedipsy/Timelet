package mk.finki.mpip.timelet.presentation.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import mk.finki.mpip.timelet.R
import mk.finki.mpip.timelet.databinding.FragmentTaskBinding
import mk.finki.mpip.timelet.network.models.Category
import mk.finki.mpip.timelet.network.models.Task
import mk.finki.mpip.timelet.network.models.toInt
import mk.finki.mpip.timelet.presentation.CategorySpinnerAdapter
import mk.finki.mpip.timelet.presentation.MainActivity
import mk.finki.mpip.timelet.viewmodels.task.TaskViewModel
import mk.finki.mpip.timelet.viewmodels.task.TaskViewState
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


class TaskFragment : Fragment() {

  companion object {
    fun createInstance() = TaskFragment()

    private val CATEGORY_ITEMS = listOf(
      Category.NO_CATEGORY,
      Category.FITNESS,
      Category.SHOPPING,
      Category.TRAVEL,
      Category.WORK,
      Category.EDUCATION
    )
  }

  private var binding: FragmentTaskBinding? = null
  private var viewModel: TaskViewModel? = null
  private var selectedDate: String? = null

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentTaskBinding.inflate(layoutInflater)
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
    binding?.apply {
      spinner.adapter = CategorySpinnerAdapter(requireContext(), CATEGORY_ITEMS)
    }
    setUpViewModel()
    setUpListeners()
  }

  private fun setUpListeners() {
    binding?.apply {
      btnSave.setOnClickListener {
        val title = etTitle.text.toString()
        val description = etDescription.text.toString()
        val token = (requireActivity() as MainActivity).token
        viewModel?.addTask(
          token,
          Task(
            title = title,
            description = description,
            dueDate = selectedDate ?: "",
            category = (spinner.selectedItem as? Category).toInt()
          )
        )
      }

      btnSelectDatetime.setOnClickListener {
        showDateTimePicker()
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

        val timePickerDialog = TimePickerDialog(
          requireContext(),
          { _, hourOfDay, minute ->
            currentDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            currentDateTime.set(Calendar.MINUTE, minute)

            // Do something with the selected date and time
            // For example, display it in a TextView
            val selectedDateTime = currentDateTime.toInstant()
            val localDateTime = LocalDateTime.ofInstant(selectedDateTime, ZoneId.systemDefault())
            selectedDate = selectedDateTime.toString()
            binding?.tvDatetime?.text =
              localDateTime.toString().let { it.take(10) + " " + it.substring(11, 16) }
          },
          currentDateTime.get(Calendar.HOUR_OF_DAY),
          currentDateTime.get(Calendar.MINUTE),
          true
        )

        timePickerDialog.show()
      },
      currentDateTime.get(Calendar.YEAR),
      currentDateTime.get(Calendar.MONTH),
      currentDateTime.get(Calendar.DAY_OF_MONTH)
    )

    datePickerDialog.show()
  }

  private fun setUpViewModel() {
    viewModel = ViewModelProvider(this)[TaskViewModel::class.java]
    viewModel?.state?.observe(viewLifecycleOwner) { state ->
      when (state) {
        TaskViewState.SUCCESS -> {
          (requireActivity() as MainActivity).showMessage(R.string.task_added_successfull)
          viewModel?.clearState()
        }
        TaskViewState.ERROR -> (requireActivity() as MainActivity).showMessage(R.string.global_error_message)
        TaskViewState.INPUT -> {
          binding?.apply {
            etTitle.text?.clear()
            etDescription.text?.clear()
            tvDatetime.text = ""
          }
        }
        else -> {}
      }
    }
  }
}