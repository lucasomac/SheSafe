package br.com.lucolimac.shesafe.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.mp.KoinPlatform.getKoin

actual fun createDatabaseBuilder(): RoomDatabase.Builder<SheSafeDatabase> {
    val appContext = getKoin().get<Context>().applicationContext
    val dbFile = appContext.getDatabasePath(SHESAFE_DB_FILE_NAME)
    return Room.databaseBuilder<SheSafeDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}
