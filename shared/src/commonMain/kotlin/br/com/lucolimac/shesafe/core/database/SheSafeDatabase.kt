package br.com.lucolimac.shesafe.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import br.com.lucolimac.shesafe.core.database.dao.HelpRequestDao
import br.com.lucolimac.shesafe.core.database.dao.SecureContactDao
import br.com.lucolimac.shesafe.core.database.entity.HelpRequestEntity
import br.com.lucolimac.shesafe.core.database.entity.SecureContactEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [
        SecureContactEntity::class,
        HelpRequestEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(SheSafeDatabaseConstructor::class)
abstract class SheSafeDatabase : RoomDatabase() {
    abstract fun secureContactDao(): SecureContactDao
    abstract fun helpRequestDao(): HelpRequestDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object SheSafeDatabaseConstructor : RoomDatabaseConstructor<SheSafeDatabase> {
    override fun initialize(): SheSafeDatabase
}

expect fun createDatabaseBuilder(): RoomDatabase.Builder<SheSafeDatabase>

fun buildSheSafeDatabase(builder: RoomDatabase.Builder<SheSafeDatabase>): SheSafeDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()

const val SHESAFE_DB_FILE_NAME = "shesafe.db"
