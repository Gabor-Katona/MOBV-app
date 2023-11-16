package eu.mcomputing.mobv.zadanie.data.api

import android.content.Context
import eu.mcomputing.mobv.zadanie.config.AppConfig
import eu.mcomputing.mobv.zadanie.data.api.helper.AuthInterceptor
import eu.mcomputing.mobv.zadanie.data.api.helper.TokenAuthenticator
import eu.mcomputing.mobv.zadanie.data.api.model.GeofenceResponse
import eu.mcomputing.mobv.zadanie.data.api.model.GeofenceUpdateRequest
import eu.mcomputing.mobv.zadanie.data.api.model.GeofenceUpdateResponse
import eu.mcomputing.mobv.zadanie.data.api.model.LoginResponse
import eu.mcomputing.mobv.zadanie.data.api.model.PasswordChangeResponse
import eu.mcomputing.mobv.zadanie.data.api.model.PasswordResetRequest
import eu.mcomputing.mobv.zadanie.data.api.model.RefreshTokenRequest
import eu.mcomputing.mobv.zadanie.data.api.model.RefreshTokenResponse
import eu.mcomputing.mobv.zadanie.data.api.model.RegistrationResponse
import eu.mcomputing.mobv.zadanie.data.api.model.UserLoginRequest
import eu.mcomputing.mobv.zadanie.data.api.model.UserRegistrationRequest
import eu.mcomputing.mobv.zadanie.data.api.model.UserResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    //@Headers("x-apikey: ${AppConfig.API_KEY}")
    @POST("user/create.php")
    suspend fun registerUser(@Body userInfo: UserRegistrationRequest): Response<RegistrationResponse>

    //@Headers("x-apikey: ${AppConfig.API_KEY}")
    @POST("user/login.php")
    suspend fun loginUser(@Body userInfo: UserLoginRequest): Response<LoginResponse>

    @GET("user/get.php")
    suspend fun getUser(
        //@HeaderMap header: Map<String, String>,
        @Query("id") id: String
    ): Response<UserResponse>

    @POST("user/refresh.php")
    suspend fun refreshToken(
        //@HeaderMap header: Map<String, String>,
        @Body refreshInfo: RefreshTokenRequest
    ): Response<RefreshTokenResponse>

    @POST("user/refresh.php")
    fun refreshTokenBlocking(
        @Body refreshInfo: RefreshTokenRequest
    ): Call<RefreshTokenResponse>

    @GET("geofence/list.php")
    suspend fun listGeofence(): Response<GeofenceResponse>

    @POST("geofence/update.php")
    suspend fun updateGeofence(@Body body: GeofenceUpdateRequest): Response<GeofenceUpdateResponse>

    @DELETE("geofence/update.php")
    suspend fun deleteGeofence(): Response<GeofenceUpdateResponse>

    @POST("user/reset.php")
    suspend fun resetPassword(
        @Body refreshInfo: PasswordResetRequest
    ): Response<PasswordChangeResponse>

    /*@POST("user/password.php")
    suspend fun changePassword(
        @Body refreshInfo: RefreshTokenRequest
    ): Response<RefreshTokenResponse>*/

    companion object{
        fun create(context: Context): ApiService {

            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .authenticator(TokenAuthenticator(context))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://zadanie.mpage.sk/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}
