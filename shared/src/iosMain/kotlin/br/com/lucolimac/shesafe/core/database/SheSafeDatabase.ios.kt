package br.com.lucolimac.shesafe.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
private fun iosDocumentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}

actual fun createDatabaseBuilder(): RoomDatabase.Builder<SheSafeDatabase> {
    val dbFilePath = "${iosDocumentDirectory()}/$SHESAFE_DB_FILE_NAME"
    return Room.databaseBuilder<SheSafeDatabase>(
        name = dbFilePath,
    )
}
