package mk.finki.mpip.timelet.presentation.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.scottyab.aescrypt.AESCrypt
import mk.finki.mpip.timelet.R
import mk.finki.mpip.timelet.databinding.ActivityLoginBinding
import mk.finki.mpip.timelet.network.NetworkUtil
import mk.finki.mpip.timelet.presentation.MainActivity
import mk.finki.mpip.timelet.presentation.base.BaseActivity
import mk.finki.mpip.timelet.viewmodels.login.LoginViewModel
import mk.finki.mpip.timelet.viewmodels.login.LoginViewState
import java.security.GeneralSecurityException

class LoginActivity : BaseActivity() {

  private var binding: ActivityLoginBinding? = null
  private var viewModel: LoginViewModel? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding?.root)
    setUp()
  }

  private fun setUp() {
    setUpViewModel()
    setUpListeners()
  }

  private fun setUpViewModel() {
    viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
    viewModel?.data?.observe(this) { data ->
      updateUi(data)
    }
  }

  private fun setUpListeners() {
    binding?.apply {
      btnLogin.setOnClickListener {
        loadingScreenVisibility(true)
        login(etUsername.text.toString(), etPassword.text.toString())
      }

      tvSignUp.setOnClickListener {
        navigateToSignUp()
      }
    }
  }

  private fun navigateToSignUp() {
    val intent = Intent(this, SignUpActivity::class.java)
    startActivity(intent)
    finish()
  }

  private fun login(username: String, password: String) {
    if (NetworkUtil.isOnline(this)) {
      viewModel?.login(username, password)
    } else {
      loadingScreenVisibility(false)
      showMessage(R.string.you_have_no_internet_connection)
    }
  }

  private fun updateUi(data: LoginViewState) {
    when (data) {
      is LoginViewState.Error -> {
        loadingScreenVisibility(false)
        showMessage(text = data.errorMessage, textRes = R.string.global_error_message)
    }
      is LoginViewState.Loading -> loadingScreenVisibility(true)
      is LoginViewState.Success -> handleLoginResult(data.token)
      else -> {} //cannot happen
    }
  }

  private fun handleLoginResult(token: String) {
    saveDetails(token)
    navigateToCalendar()
  }

  private fun saveDetails(token: String) {
    var encryptedToken: String? = null
    val passwordToken = "password"
    try {
      encryptedToken = AESCrypt.encrypt(passwordToken, token)
    } catch (e: GeneralSecurityException) {
      showMessage(R.string.global_error_message)
    }
    val sp = this.getSharedPreferences("pref", Context.MODE_PRIVATE)
    val editor = sp.edit()
    editor.putString("token", encryptedToken)
    editor.apply()
  }

  private fun navigateToCalendar() {
    val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
    finish()
  }

  private fun loadingScreenVisibility(visible: Boolean) {
    binding?.apply {
      loadingLayout.root.isVisible = visible
    }
  }

}