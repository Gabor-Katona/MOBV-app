package eu.mcomputing.mobv.zadanie.data.api

import android.content.Context
import android.util.Log
import eu.mcomputing.mobv.zadanie.config.AppConfig
import eu.mcomputing.mobv.zadanie.data.api.model.GeofenceUpdateRequest
import eu.mcomputing.mobv.zadanie.data.api.model.RefreshTokenRequest
import eu.mcomputing.mobv.zadanie.data.api.model.UserLoginRequest
import eu.mcomputing.mobv.zadanie.data.model.User
import eu.mcomputing.mobv.zadanie.data.api.model.UserRegistrationRequest
import eu.mcomputing.mobv.zadanie.data.db.AppRoomDatabase
import eu.mcomputing.mobv.zadanie.data.db.LocalCache
import eu.mcomputing.mobv.zadanie.data.db.entities.GeofenceEntity
import eu.mcomputing.mobv.zadanie.data.db.entities.UserEntity
import java.io.IOException

class DataRepository private constructor(
    private val service: ApiService,
    private val cache: LocalCache
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
                        LocalCache(AppRoomDatabase.getInstance(context).appDao())
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

}