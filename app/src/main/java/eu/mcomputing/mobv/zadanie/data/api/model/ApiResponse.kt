package eu.mcomputing.mobv.zadanie.data.api.model

data class RegistrationResponse(
    val uid: String,
    val access: String,
    val refresh: String
)

data class LoginResponse(
    val uid: String,
    val access: String,
    val refresh: String
)

data class UserResponse(
    val id: String,
    val name: String,
    val photo: String
)

data class RefreshTokenResponse(
    val uid: String,
    val access: String,
    val refresh: String
)
