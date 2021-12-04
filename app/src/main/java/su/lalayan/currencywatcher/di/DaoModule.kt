package su.lalayan.currencywatcher.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import su.lalayan.currencywatcher.data.local.FavoriteRatesDao
import su.lalayan.currencywatcher.data.local.FavoriteRatesDB
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Singleton
    @Provides
    fun provideBlogDb(@ApplicationContext context: Context): FavoriteRatesDB {
        return Room
            .databaseBuilder(
                context,
                FavoriteRatesDB::class.java,
                FavoriteRatesDB.DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideBlogDAO(favoriteRatesDB: FavoriteRatesDB): FavoriteRatesDao {
        return favoriteRatesDB.blogDao()
    }
}