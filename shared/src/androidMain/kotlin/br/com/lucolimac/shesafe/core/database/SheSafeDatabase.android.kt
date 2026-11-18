package br.com.lucolimac.shesafe.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.mp.KoinPlatform.getKoin

/**
 * Android implementation of [createDatabaseBuilder] backed by the application's database directory.
 *
 * Resolves the application [Context] from Koin and configures Room to store [SheSafeDatabase]
 * at [SHESAFE_DB_FILE_NAME].
 *
 * @return Unbuilt [RoomDatabase.Builder] targeting the Android app database file path.
 */
actual fun createDatabaseBuilder(): RoomDatabase.Builder<SheSafeDatabase> {
    val appContext = getKoin().get<Context>().applicationContext
    val dbFile = appContext.getDatabasePath(SHESAFE_DB_FILE_NAME)
    return Room.databaseBuilder<SheSafeDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}
