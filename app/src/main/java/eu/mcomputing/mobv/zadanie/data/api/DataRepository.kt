package eu.mcomputing.mobv.zadanie.data.api

import android.content.Context
import android.net.Uri
import android.util.Log
import eu.mcomputing.mobv.zadanie.data.api.model.GeofenceUpdateRequest
import eu.mcomputing.mobv.zadanie.data.api.model.PasswordChangeRequest
import eu.mcomputing.mobv.zadanie.data.api.model.PasswordResetRequest
import eu.mcomputing.mobv.zadanie.data.api.model.UserLoginRequest
import eu.mcomputing.mobv.zadanie.data.api.model.UserRegistrationRequest
import eu.mcomputing.mobv.zadanie.data.db.AppRoomDatabase
import eu.mcomputing.mobv.zadanie.data.db.LocalCache
import eu.mcomputing.mobv.zadanie.data.db.entities.GeofenceEntity
import eu.mcomputing.mobv.zadanie.data.db.entities.UserEntity
import eu.mcomputing.mobv.zadanie.data.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException


class DataRepository private constructor(
    private val service: ApiService,
    private val cache: LocalCache,
    private val pictureService: ImageApiService,
) {
    companion object {
        const val TAG = "DataRepository"

        @Volatile
        private var INSTANCE: DataRepository? = null
        private val lock = Any()

        fun getInstance(context: Context): DataRepository =
            INSTANCE ?: synchronized(lock) {
                INSTANCE
                    ?: DataRepository(
                        ApiService.create(context),
                        LocalCache(AppRoomDatabase.getInstance(context).appDao()),
                        ImageApiService.create(context)
                    ).also { INSTANCE = it }
            }
    }

    suspend fun apiRegisterUser(username: String, email: String, password: String) : Pair<String, User?>{
        if (username.isEmpty()) {
            return Pair("Username can not be empty", null)
        }
        if (email.isEmpty()) {
            return Pair("Email can not be empty", null)
        }
        if (password.isEmpty()) {
            return Pair("Password can not be empty", null)
        }

        try {
            val response = service.registerUser(UserRegistrationRequest(username, email, password))

            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    return Pair(
                        "",
                        User(
                            username,
                            email,
                            json_response.uid,
                            json_response.access,
                            json_response.refresh
                        )
                    )
                }
            }
            return Pair("Failed to create user", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to create user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return Pair("Fatal error. Failed to create user.", null)
    }

    suspend fun apiLoginUser(username: String, password: String): Pair<String, User?> {
        if (username.isEmpty()) {
            return Pair("Username can not be empty", null)
        }
        if (password.isEmpty()) {
            return Pair("Password can not be empty", null)
        }

        try {
            val response = service.loginUser(UserLoginRequest(username, password))

            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    if (json_response.uid == "-1") {
                        return Pair("Wrong password or username.", null)
                    }
                    return Pair(
                        "",
                        User(
                            username,
                            "",
                            json_response.uid,
                            json_response.access,
                            json_response.refresh
                        )
                    )
                }
            }
            return Pair("Failed to login user", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to login user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return Pair("Fatal error. Failed to login user.", null)
    }


    suspend fun apiGetUser(
        uid: String,
    ): Pair<String, User?> {
        try {
            val response = service.getUser(uid)

            if (response.isSuccessful) {
                response.body()?.let {
                    val user = User(it.name, "", it.id, "", "", it.photo)
                    cache.insertUserItems(
                        listOf(
                            UserEntity(
                                user.id,
                                user.username,
                                "",
                                0.0,
                                0.0,
                                0.0,
                                ""
                            )
                        )
                    )
                    return Pair("", user)
                }
            }
            // in case that assess token is expired AuthInterceptor handles it

            return Pair("Failed to load user", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to load user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to load user.", null)
    }

    suspend fun apiGeofenceUsers(): String {
        try {
            val response = service.listGeofence()
            /*val user = UserEntity(
                "id1", "gabo", "true",
                425.52, 75.25, 100.0, "link")
            val userList = listOf(user)
            cache.insertUserItems(userList)*/
            Log.d("DataRepository", "apiGeofenceUsers")

            if (response.isSuccessful) {
                response.body()?.let { resp ->
                    val users = resp.list.map {
                        UserEntity(
                            it.uid,
                            it.name,
                            it.updated,
                            resp.me.lat,
                            resp.me.lon,
                            it.radius,
                            it.photo
                        )
                    }

                    cache.insertUserItems(users)

                    return ""
                }
            }

            return "Failed to load user"
        } catch (ex: IOException) {
            ex.printStackTrace()
            return "Check internet connection. Failed to load user."
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return "Fatal error. Failed to load user."
    }

    fun getUsers() = cache.getUsers()

    suspend fun getUsersList() = cache.getUsersList()

    suspend fun insertGeofence(item: GeofenceEntity) {
        cache.insertGeofence(item)
        try {
            val response =
                service.updateGeofence(GeofenceUpdateRequest(item.lat, item.lon, item.radius))

            if (response.isSuccessful) {
                response.body()?.let {

                    item.uploaded = true
                    cache.insertGeofence(item)
                    return
                }
            }

            return
        } catch (ex: IOException) {
            ex.printStackTrace()
            return
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    suspend fun removeGeofence() {
        try {
            val response = service.deleteGeofence()

            if (response.isSuccessful) {
                response.body()?.let {
                    return
                }
            }

            return
        } catch (ex: IOException) {
            ex.printStackTrace()
            return
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    suspend fun apiResetPassword(email: String): String {
        if (email.isEmpty()) {
            return "Email can not be empty"
        }

        try {
            val response = service.resetPassword(PasswordResetRequest(email))

            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    if (json_response.status == "failure") {
                        return json_response.message
                    }
                    return "Check your email to reset password"

                }
            }
            return "Failed to reset password"
        } catch (ex: IOException) {
            ex.printStackTrace()
            return "Check internet connection. Failed to reset password."
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return "Fatal error. Failed to reset password."
    }

    suspend fun apiChangePassword(oldPassword: String, newPassword: String): String {
        if (oldPassword.isEmpty()) {
            return "Old password can not be empty"
        }
        if (newPassword.isEmpty()) {
            return "New password can not be empty"
        }

        try {
            val response = service.changePassword(PasswordChangeRequest(oldPassword, newPassword))

            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    return "Password successfully changed"

                }
            }
            return "Failed to change password"
        } catch (ex: IOException) {
            ex.printStackTrace()
            return "Check internet connection. Failed to change password."
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return "Fatal error. Failed to change password."
    }

    suspend fun apiUploadProfilePicture(picturePath: String): String {
        if (picturePath.isEmpty()) {
            return "Picture path can not be empty"
        }

        //val file = File("/storage/emulated/0/Download/man-avatar-profile-picture-vector-illustration_268834-538.jpg")
        val file = File(picturePath)
        val requestFile = file.asRequestBody(MultipartBody.FORM)

        // MultipartBody.Part is used to send also the actual file name
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

        try {
            val response = pictureService.uploadProfilePicture(body)

            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    Log.d("DataRepository", json_response.toString())
                    return "Picture successfully uploaded"
                }
            }
            return "Failed to upload picture"
        } catch (ex: IOException) {
            ex.printStackTrace()
            return "Check internet connection. Failed to upload picture."
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return "Fatal error. Failed to upload picture."
    }

}