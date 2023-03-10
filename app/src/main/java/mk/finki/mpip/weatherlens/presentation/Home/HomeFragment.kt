package mk.finki.mpip.weatherlens.presentation.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import mk.finki.mpip.weatherlens.databinding.FragmentHomeBinding
import mk.finki.mpip.weatherlens.viewmodels.HomeViewModel
import mk.finki.mpip.weatherlens.viewmodels.HomeViewState

class HomeFragment : Fragment() {

  companion object {
    fun createInstance() = HomeFragment()
  }

  private var binding: FragmentHomeBinding? = null
  private var viewModel: HomeViewModel? = null

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentHomeBinding.inflate(layoutInflater)
    return binding?.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

    viewModel?.data?.observe(viewLifecycleOwner) { data ->
      updateUi(data)
    }
  }

  private fun updateUi(data: HomeViewState) {
    when (data) {
      is HomeViewState.Error -> showErrorScreen()
      is HomeViewState.Loading -> showLoadingScreen()
      is HomeViewState.WeatherContent -> showContentScreen(data)
    }
  }

  private fun showContentScreen(data: HomeViewState.WeatherContent) {
    binding?.apply {
      contentLayout.isVisible = true
      loadingLayout.isVisible = false
      errorLayout.isVisible = false

      name.text = data.text
    }
  }

  private fun showLoadingScreen() {
    binding?.apply {
      loadingLayout.isVisible = true
      contentLayout.isVisible = false
      errorLayout.isVisible = false
    }
  }

  private fun showErrorScreen() {
    binding?.apply {
      errorLayout.isVisible = true
      loadingLayout.isVisible = false
      contentLayout.isVisible = false
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding = null
  }
}