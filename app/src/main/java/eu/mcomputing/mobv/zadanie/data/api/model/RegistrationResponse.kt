package eu.mcomputing.mobv.zadanie.data.api.model

data class RegistrationResponse(
    val uid: String,
    val access: String,
    val refresh: String
)
