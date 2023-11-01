package eu.mcomputing.mobv.zadanie.data.api

import eu.mcomputing.mobv.zadanie.config.AppConfig
import eu.mcomputing.mobv.zadanie.data.api.model.RefreshTokenRequest
import eu.mcomputing.mobv.zadanie.data.api.model.UserLoginRequest
import eu.mcomputing.mobv.zadanie.data.model.User
import eu.mcomputing.mobv.zadanie.data.api.model.UserRegistrationRequest
import java.io.IOException

class DataRepository private constructor(
    private val service: ApiService
) {
    companion object {
        const val TAG = "DataRepository"

        @Volatile
        private var INSTANCE: DataRepository? = null
        private val lock = Any()

        fun getInstance(): DataRepository =
            INSTANCE ?: synchronized(lock) {
                INSTANCE
                    ?: DataRepository(ApiService.create()).also { INSTANCE = it }
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
        my_uid: String,
        accessToken: String,
        refreshToken: String
    ): Pair<String, User?> {
        try {
            val response = service.getUser(
                mapOf(
                    "x-apikey" to AppConfig.API_KEY,
                    "Authorization" to "Bearer $accessToken"
                ), uid
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    return Pair(
                        "",
                        User(
                            it.name,
                            "",
                            it.id,
                            accessToken,
                            refreshToken,
                            it.photo
                        )
                    )
                }
            }

            if (response.code() == 401) {
                // response is 401, user token is valid
                val refreshResponse = service.refreshToken(
                    mapOf(
                        "x-apikey" to AppConfig.API_KEY,
                        "x-user" to my_uid
                    ), RefreshTokenRequest(refreshToken)
                )

                if (refreshResponse.isSuccessful) {
                    refreshResponse.body()?.let { newtoken ->
                        val response2 = service.getUser(
                            mapOf(
                                "x-apikey" to AppConfig.API_KEY,
                                "Authorization" to "Bearer ${newtoken.access}"
                            ), uid
                        )
                        if (response2.isSuccessful) {
                            response2.body()?.let {
                                return Pair(
                                    "",
                                    User(
                                        it.name,
                                        "",
                                        it.id,
                                        newtoken.access,
                                        newtoken.refresh,
                                        it.photo
                                    )
                                )
                            }
                        }
                    }
                }
            }
            return Pair("Failed to load user", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to load user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to load user.", null)
    }

}