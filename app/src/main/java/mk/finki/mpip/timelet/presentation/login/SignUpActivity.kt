package mk.finki.mpip.timelet.presentation.login

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import mk.finki.mpip.timelet.R
import mk.finki.mpip.timelet.databinding.ActivitySignUpBinding
import mk.finki.mpip.timelet.network.NetworkUtil
import mk.finki.mpip.timelet.presentation.base.BaseActivity
import mk.finki.mpip.timelet.viewmodels.signup.SignUpViewModel
import mk.finki.mpip.timelet.viewmodels.signup.SignUpViewState

class SignUpActivity : BaseActivity() {

  private var binding: ActivitySignUpBinding? = null
  private var viewModel: SignUpViewModel? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivitySignUpBinding.inflate(layoutInflater)
    setContentView(binding?.root)
    setUp()
  }

  private fun setUp() {
    setUpViewModel()
    setUpListeners()
  }

  private fun setUpViewModel() {
    viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
    viewModel?.data?.observe(this) { data ->
      updateUi(data)
    }
  }

  private fun setUpListeners() {
    binding?.apply {
      btnSignUp.setOnClickListener {
        loadingScreenVisibility(true)
        signUp(etUsername.text.toString(), etPassword.text.toString(), etConfirmPassword.text.toString())
      }

      tvLogIn.setOnClickListener {
        navigateToLogin()
      }
    }
  }

  private fun signUp(username: String, password: String, confirmPassword: String) {
    if (NetworkUtil.isOnline(this)) {
      viewModel?.signUp(username, password, confirmPassword)
    } else {
      loadingScreenVisibility(false)
      showMessage(R.string.you_have_no_internet_connection)
    }
  }

  private fun updateUi(data: SignUpViewState) {
    when (data) {
      is SignUpViewState.Error -> {
        loadingScreenVisibility(false)
        showMessage(text = data.errorMessage, textRes = R.string.global_error_message)
      }
      is SignUpViewState.Loading -> loadingScreenVisibility(true)
      is SignUpViewState.Success -> handleSignUpResult()
      else -> {} //cannot happen
    }
  }

  private fun handleSignUpResult() {
    showMessage(textRes = R.string.successfull_register)
    navigateToLogin()
  }

  private fun navigateToLogin() {
    val intent = Intent(this, LoginActivity::class.java)
    startActivity(intent)
    finish()
  }

  private fun loadingScreenVisibility(visible: Boolean) {
    binding?.apply {
      loadingLayout.root.isVisible = visible
    }
  }

}