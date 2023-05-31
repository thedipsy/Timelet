package mk.finki.mpip.timelet.network.service

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitService {

  companion object {

    @Volatile
    private var INSTANCE: TimeletApi? = null

    @JvmStatic
    fun api(): TimeletApi {
      return INSTANCE ?: synchronized(this) {
        val instance = createTimeletApi()
        INSTANCE = instance
        instance
      }
    }

    private const val BASE_URL = "https://timeletapi.azurewebsites.net/timelet/"

    private fun createTimeletApi(): TimeletApi {
      val gson = GsonBuilder()
        .setLenient()
        .create()
      val gsonConverterFactory = GsonConverterFactory.create(gson)

      val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Set connection timeout
        .readTimeout(30, TimeUnit.SECONDS) // Set read timeout
        .build()

      return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(gsonConverterFactory)
        .build()
        .create(TimeletApi::class.java)
    }
  }
}