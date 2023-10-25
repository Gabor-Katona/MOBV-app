package eu.mcomputing.mobv.zadanie.data.api

import eu.mcomputing.mobv.zadanie.config.AppConfig
import eu.mcomputing.mobv.zadanie.data.api.model.LoginResponse
import eu.mcomputing.mobv.zadanie.data.api.model.RegistrationResponse
import eu.mcomputing.mobv.zadanie.data.api.model.UserLoginRequest
import eu.mcomputing.mobv.zadanie.data.api.model.UserRegistrationRequest
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers("x-apikey: ${AppConfig.API_KEY}")
    @POST("user/create.php")
    suspend fun registerUser(@Body userInfo: UserRegistrationRequest): Response<RegistrationResponse>

    @Headers("x-apikey: ${AppConfig.API_KEY}")
    @POST("user/login.php")
    suspend fun loginUser(@Body userInfo: UserLoginRequest): Response<LoginResponse>

    companion object{
        fun create(): ApiService {

            val retrofit = Retrofit.Builder()
                .baseUrl("https://zadanie.mpage.sk/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}
