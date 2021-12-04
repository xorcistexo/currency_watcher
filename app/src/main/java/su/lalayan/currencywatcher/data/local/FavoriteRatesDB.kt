package su.lalayan.currencywatcher.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteRatesEntity::class], version = 1)
abstract class FavoriteRatesDB : RoomDatabase() {

    abstract fun blogDao(): FavoriteRatesDao

    companion object {
        val DATABASE_NAME: String = "favorite_rates_db"
    }
}