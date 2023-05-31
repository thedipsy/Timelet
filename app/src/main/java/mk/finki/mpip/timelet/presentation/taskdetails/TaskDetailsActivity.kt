package mk.finki.mpip.timelet.presentation.taskdetails

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.scottyab.aescrypt.AESCrypt
import mk.finki.mpip.timelet.R
import mk.finki.mpip.timelet.databinding.ActivityTaskDetailsBinding
import mk.finki.mpip.timelet.network.models.Category
import mk.finki.mpip.timelet.network.models.Task
import mk.finki.mpip.timelet.network.models.toCategory
import mk.finki.mpip.timelet.network.models.toInt
import mk.finki.mpip.timelet.presentation.CategorySpinnerAdapter
import mk.finki.mpip.timelet.presentation.base.BaseActivity
import mk.finki.mpip.timelet.viewmodels.taskdetails.TaskDetailsViewModel
import mk.finki.mpip.timelet.viewmodels.taskdetails.TaskDetailsViewState
import java.security.GeneralSecurityException
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class TaskDetailsActivity : BaseActivity() {

  companion object {
    private val CATEGORY_ITEMS = listOf(
      Category.NO_CATEGORY,
      Category.FITNESS,
      Category.SHOPPING,
      Category.TRAVEL,
      Category.WORK,
      Category.EDUCATION
    )
  }

  private var viewModel: TaskDetailsViewModel? = null
  private var binding: ActivityTaskDetailsBinding? = null
  var token: String? = null
  private var selectedDate: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
    setContentView(binding?.root)
    getToken()
    setUpViewModel()
    setUpView()
    setUpListeners()
  }

  private fun setUpViewModel() {
    viewModel = ViewModelProvider(this)[TaskDetailsViewModel::class.java]
    viewModel?.state?.observe(this) { state ->
      when (state) {
        is TaskDetailsViewState.Success -> {
          showMessage(state.textRes)
          finish()
        }
        is TaskDetailsViewState.Error -> {
          showMessage(state.textRes)
        }
        else -> {}
      }
    }
  }

  private fun setUpListeners() {
    binding?.apply {
      btnSelectDatetime.setOnClickListener {
        showDateTimePicker()
      }
      btnUpdate.setOnClickListener {
        val title = etTitle.text.toString()
        val description = etDescription.text.toString()
        val id = intent.getIntExtra("id", -1)
        viewModel?.updateTask(token, Task(id, title, description, selectedDate ?: "", (spinner.selectedItem as? Category).toInt()))
      }
      btnDelete.setOnClickListener {
        AlertDialog.Builder(this@TaskDetailsActivity, R.style.AlertDialogTheme)
          .setTitle("Delete task")
          .setMessage("Are you sure to delete the task?")
          .setNegativeButton("DELETE") { dialog, _ ->
            val id = intent.getIntExtra("id", -1)
            viewModel?.deleteTask(token, id)
            dialog.dismiss()
          }
          .setPositiveButton("CANCEL") { dialog, _ ->
            dialog.dismiss()
          }
          .create()
          .show()
      }
    }
  }

  private fun showDateTimePicker() {
    val currentDateTime = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
      this,
      { _, year, month, dayOfMonth ->
        currentDateTime.set(Calendar.YEAR, year)
        currentDateTime.set(Calendar.MONTH, month)
        currentDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val timePickerDialog = TimePickerDialog(
          this,
          { _, hourOfDay, minute ->
            currentDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            currentDateTime.set(Calendar.MINUTE, minute)

            // Do something with the selected date and time
            // For example, display it in a TextView
            val selectedDateTime = currentDateTime.toInstant()
            val localDateTime = LocalDateTime.ofInstant(selectedDateTime, ZoneId.systemDefault())
            selectedDate = localDateTime.toString()
            binding?.tvDatetime?.text =
              localDateTime.toString().let { it.take(10) + " " + it.substring(11,16) }
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

  private fun setUpView() {
    val title = intent.getStringExtra("title")
    val description = intent.getStringExtra("description")
    val dueDate = intent.getStringExtra("dueDate")
    val category = intent.getIntExtra("category", -1)
    selectedDate = dueDate
    binding?.apply {
      spinner.adapter = CategorySpinnerAdapter(this@TaskDetailsActivity, CATEGORY_ITEMS)
      if (category != -1) {
        spinner.setSelection(CATEGORY_ITEMS.indexOf(category.toCategory()))
      }

      etTitle.setText(title)
      etDescription.setText(description)
      tvDatetime.text = dueDate?.let { it.take(10) + " " + it.substring(11,16)  }
    }
  }

  private fun getToken() {
    val sp = getSharedPreferences("pref", Context.MODE_PRIVATE)
    val encryptedMessage = sp.getString("token", "").toString()
    val password = "password"
    return try {
      val decryptedToken = AESCrypt.decrypt(password, encryptedMessage)
      token = "Bearer $decryptedToken"
    } catch (e: GeneralSecurityException) {
      showMessage(R.string.global_error_message)
    }
  }
}