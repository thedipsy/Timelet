package mk.finki.mpip.timelet.network

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object NetworkUtil {

  private const val CAPTIVE_PORTAL_CHECK_URL = "http://connectivitycheck.gstatic.com/generate_204"
  private const val CAPTIVE_PORTAL_SOCKET_TIMEOUT_MS = 1000

  /**
   * Checks for internet connection.
   */
  fun isOnline(context: Context): Boolean {
    val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE)
      as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return isInternetAvailable(networkCapabilities) && !isCaptivePortalConnection(networkCapabilities)
  }

  private fun isInternetAvailable(networkCapabilities: NetworkCapabilities): Boolean {
    return when {
      networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
      networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
      networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
      networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
      networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
      else -> false
    }
  }

  private fun isCaptivePortalConnection(networkCapabilities: NetworkCapabilities): Boolean {
    if (!networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
      return false
    } else {
      var urlConnection: HttpURLConnection? = null
      return try {
        val url = URL(CAPTIVE_PORTAL_CHECK_URL)
        urlConnection = url.openConnection() as HttpURLConnection?
        urlConnection?.connectTimeout = CAPTIVE_PORTAL_SOCKET_TIMEOUT_MS
        urlConnection?.readTimeout = CAPTIVE_PORTAL_SOCKET_TIMEOUT_MS
        urlConnection?.useCaches = false

        urlConnection?.responseCode != 204
      } catch (e: IOException) {
        false
      } finally {
        urlConnection?.disconnect()
      }
    }
  }
}