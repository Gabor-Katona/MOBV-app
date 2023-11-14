package eu.mcomputing.mobv.zadanie.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import eu.mcomputing.mobv.zadanie.data.db.entities.GeofenceEntity
import eu.mcomputing.mobv.zadanie.data.db.entities.UserEntity

@Database(
    entities = [
        UserEntity::class,
        GeofenceEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun appDao(): DbDao

    companion object {
        @Volatile
        private var INSTANCE: AppRoomDatabase? = null

        // get instance of Singleton
        fun getInstance(context: Context): AppRoomDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        /**
         * creates new instance of AppRoomDatabase
         */
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppRoomDatabase::class.java, "treeCamDB"
            ).fallbackToDestructiveMigration()
                .build()
    }
}