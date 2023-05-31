package mk.finki.mpip.timelet.network.service

import mk.finki.mpip.timelet.network.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TimeletApi {

  @POST("user/login")
  suspend fun login(@Body loginRequest: LoginRequest) :  Response<BaseResponse>

  @POST("user/register")
  suspend fun register(@Body signUpRequest: SignUpRequest) :  Response<BaseResponse>

  @GET("tasks")
  suspend fun getTasks(@Header("Authorization") token: String) : Response<TasksResponse>

  @POST("tasks")
  suspend fun search(@Header("Authorization") token: String, @Body searchRequest: SearchRequest) : Response<TasksResponse>

  @GET("tasks/{id}")
  suspend fun getTask(@Header("Authorization") token: String, @Path("id") id: Int) : Response<TasksResponse>

  @POST("tasks/create")
  suspend fun saveTask(@Header("Authorization") token: String, @Body task: Task) : Response<TasksResponse>

  @PATCH("tasks/{id}")
  suspend fun updateTask(@Header("Authorization") token: String, @Path("id") id: Int, @Body task: Task) : Response<BaseResponse>

  @DELETE("tasks/{id}")
  suspend fun deleteTask(@Header("Authorization") token: String, @Path("id") id: Int) : Response<BaseResponse>

}