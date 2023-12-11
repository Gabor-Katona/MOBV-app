package eu.mcomputing.mobv.zadanie.data

import android.content.Context
import android.content.SharedPreferences
import eu.mcomputing.mobv.zadanie.config.AppConfig
import eu.mcomputing.mobv.zadanie.data.model.User
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class PreferenceData private constructor() {

    private fun getSharedPreferences(context: Context?): SharedPreferences? {
        return context?.getSharedPreferences(
            shpKey, Context.MODE_PRIVATE
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: PreferenceData? = null

        private val lock = Any()

        fun getInstance(): PreferenceData =
            INSTANCE ?: synchronized(lock) {
                INSTANCE
                    ?: PreferenceData().also { INSTANCE = it }
            }

        private const val shpKey = AppConfig.SharedPreferences_KEY
        private const val userKey = "userKey"
        private const val sharingKey = "sharingKey"
        private const val startSharingTimeKey = "startSharingTimeKey"
        private const val endSharingTimeKey = "endSharingTimeKey"

    }

    fun clearData(context: Context?) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

    fun putUser(context: Context?, user: User?) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        user?.toJson()?.let {
            editor.putString(userKey, it)
        } ?: editor.remove(userKey)

        editor.apply()
    }

    fun getUser(context: Context?): User? {
        val sharedPref = getSharedPreferences(context) ?: return null
        val json = sharedPref.getString(userKey, null) ?: return null

        return User.fromJson(json)
    }

    fun putSharing(context: Context?, sharing: Boolean) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        editor.putBoolean(sharingKey, sharing)
        editor.apply()
    }

    fun getSharing(context: Context?): Boolean {
        val sharedPref = getSharedPreferences(context) ?: return false
        val sharing = sharedPref.getBoolean(sharingKey, false)

        return sharing
    }

    fun putStartSharingTime(context: Context?, time: LocalTime?) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        val timeAsString = time?.format(DateTimeFormatter.ISO_LOCAL_TIME)
        editor.putString(startSharingTimeKey, timeAsString)
        editor.apply()
    }

    fun getStartSharingTime(context: Context?): LocalTime? {
        val sharedPref = getSharedPreferences(context) ?: return null
        val timeAsString = sharedPref.getString(startSharingTimeKey, null)

        return if (timeAsString != null) {
            LocalTime.parse(timeAsString, DateTimeFormatter.ISO_LOCAL_TIME)
        } else {
            null
        }
    }

    fun putEndSharingTime(context: Context?, time: LocalTime?) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        val timeAsString = time?.format(DateTimeFormatter.ISO_LOCAL_TIME)
        editor.putString(endSharingTimeKey, timeAsString)
        editor.apply()
    }

    fun getEndSharingTime(context: Context?): LocalTime? {
        val sharedPref = getSharedPreferences(context) ?: return null
        val timeAsString = sharedPref.getString(endSharingTimeKey, null)

        return if (timeAsString != null) {
            LocalTime.parse(timeAsString, DateTimeFormatter.ISO_LOCAL_TIME)
        } else {
            null
        }
    }

}