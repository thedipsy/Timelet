package mk.finki.mpip.timelet.presentation

import android.content.Context
import android.os.Bundle
import com.scottyab.aescrypt.AESCrypt
import mk.finki.mpip.timelet.R
import mk.finki.mpip.timelet.databinding.ActivityMainBinding
import mk.finki.mpip.timelet.presentation.base.BaseActivity
import mk.finki.mpip.timelet.presentation.calendar.CalendarFragment
import mk.finki.mpip.timelet.presentation.task.TaskFragment
import java.security.GeneralSecurityException

class MainActivity : BaseActivity() {

  private var binding: ActivityMainBinding? = null
  var token: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding?.root)
    getToken()
    setupView()
    navigateToCalendar()
  }

  private fun setupView() {
    binding?.bottomNavigation?.apply {
      buttonCalendar.setOnClickListener {
        navigateToCalendar()
      }
      buttonTask.setOnClickListener {
        navigateToTask()
      }
    }
  }

  private fun navigateToCalendar() {
    binding?.bottomNavigation?.apply {
      ivCalendar.setImageDrawable(getDrawable(R.drawable.calendar_selected))
      ivTask.setImageDrawable(getDrawable(R.drawable.task))
    }

    navigateToFragment(fragment = CalendarFragment.createInstance())
  }

  private fun navigateToTask() {
    binding?.bottomNavigation?.apply {
    ivTask.setImageDrawable(getDrawable(R.drawable.task_selected))
    ivCalendar.setImageDrawable(getDrawable(R.drawable.calendar))
    }
    navigateToFragment(fragment = TaskFragment.createInstance())
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