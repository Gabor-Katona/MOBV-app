package eu.mcomputing.mobv.zadanie.data.api

import android.content.Context
import eu.mcomputing.mobv.zadanie.data.api.helper.AuthInterceptor
import eu.mcomputing.mobv.zadanie.data.api.helper.TokenAuthenticator
import eu.mcomputing.mobv.zadanie.data.api.model.PictureUploadResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImageApiService {

    @Multipart
    @POST("user/photo.php")
    suspend fun uploadProfilePicture(
        @Part image: MultipartBody.Part
    ): Response<PictureUploadResponse>

    @DELETE("user/photo.php")
    suspend fun deleteProfilePicture(): Response<PictureUploadResponse>


    companion object{
        fun create(context: Context): ImageApiService {

            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .authenticator(TokenAuthenticator(context))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://upload.mcomputing.eu/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ImageApiService::class.java)
        }
    }
}