package su.lalayan.currencywatcher.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface FavoriteRatesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(blogEntity: FavoriteRatesEntity): Long

    @Query("SELECT * FROM blogs")
    suspend fun get(): List<FavoriteRatesEntity>
}