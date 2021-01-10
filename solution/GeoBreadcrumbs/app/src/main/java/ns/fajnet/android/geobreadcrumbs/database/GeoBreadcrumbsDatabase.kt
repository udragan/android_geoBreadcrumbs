package ns.fajnet.android.geobreadcrumbs.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Track::class, Place::class, Point::class], version = 1, exportSchema = false)
abstract class GeoBreadcrumbsDatabase : RoomDatabase() {

    // Dao declarations ----------------------------------------------------------------------------

    abstract val trackDao: TrackDao
    abstract val placeDao: PlaceDao
    abstract val pointDao: PointDao

    // companion -----------------------------------------------------------------------------------

    companion object {
        @Volatile
        private var INSTANCE: GeoBreadcrumbsDatabase? = null

        fun getInstance(context: Context): GeoBreadcrumbsDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        GeoBreadcrumbsDatabase::class.java,
                        "tracking_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}
