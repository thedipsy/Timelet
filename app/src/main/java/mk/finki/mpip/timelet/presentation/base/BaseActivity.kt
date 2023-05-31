package mk.finki.mpip.timelet.presentation.base

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import mk.finki.mpip.timelet.R
import mk.finki.mpip.timelet.presentation.calendar.CalendarFragment

abstract class BaseActivity : AppCompatActivity() {

  fun showMessage(@StringRes textRes: Int, text: String? = null) =
    Toast.makeText(
      this,
      text ?: getString(textRes),
      Toast.LENGTH_LONG
    ).show()

  fun navigateToFragment(fragment: Fragment, tag: String = fragment::class.java.simpleName) {
    supportFragmentManager.findFragmentByTag(tag)?.let {
      replaceFragment(it, tag)
    } ?: addFragment(fragment, tag)
  }

  override fun onBackPressed() {
    navigateToHomeOrFinish()
  }

  private fun navigateToHomeOrFinish() {
    if (supportFragmentManager.backStackEntryCount > 1) {
      supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
      replaceFragment(CalendarFragment.createInstance())
    } else {
      finish()
    }
  }

  private fun replaceFragment(fragment: Fragment, tag: String = fragment::class.java.simpleName) =
    supportFragmentManager
      .beginTransaction()
      .replace(R.id.fragmentContainer, fragment, tag)
      .commit()

  private fun addFragment(fragment: Fragment, tag: String = fragment::class.java.simpleName) =
    supportFragmentManager
      .beginTransaction()
      .replace(R.id.fragmentContainer, fragment, tag)
      .addToBackStack(tag)
      .commit()
}

