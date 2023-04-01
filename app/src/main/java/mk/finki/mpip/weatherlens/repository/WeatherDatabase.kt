package mk.finki.mpip.weatherlens.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ImageCapture::class], version = 1, exportSchema = false)
@TypeConverters(BitmapConverter::class, LocalDateTimeConverter::class)
abstract class WeatherDatabase : RoomDatabase() {

  companion object {
    @Volatile
    private var INSTANCE: WeatherDatabase? = null

    fun getDatabase(context: Context): WeatherDatabase {

      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          WeatherDatabase::class.java,
          "weather_database"
        ).build()
        INSTANCE = instance
        // return instance
        instance
      }
    }
  }

  abstract fun getImagesDao(): WeatherDao
}