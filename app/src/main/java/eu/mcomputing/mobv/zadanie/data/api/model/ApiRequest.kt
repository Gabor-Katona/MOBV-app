package eu.mcomputing.mobv.zadanie.data.api.model

data class UserRegistrationRequest(
    val name: String,
    val email: String,
    val password: String
)

data class UserLoginRequest(
    val name: String,
    val password: String
)