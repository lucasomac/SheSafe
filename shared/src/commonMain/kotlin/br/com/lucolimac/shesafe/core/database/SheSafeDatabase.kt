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

/**
 * Primary Room database for local offline-first persistence in SheSafe.
 *
 * Stores trusted emergency contacts ([SecureContactEntity]) and historical SOS dispatches ([HelpRequestEntity]).
 */
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
    /**
     * Provides the Data Access Object for managing [SecureContactEntity] records.
     *
     * @return The [SecureContactDao] bound to this database instance.
     */
    abstract fun secureContactDao(): SecureContactDao

    /**
     * Provides the Data Access Object for managing [HelpRequestEntity] records.
     *
     * @return The [HelpRequestDao] bound to this database instance.
     */
    abstract fun helpRequestDao(): HelpRequestDao
}

/**
 * Multiplatform Room compiler-generated constructor used to instantiate [SheSafeDatabase] on non-JVM targets.
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object SheSafeDatabaseConstructor : RoomDatabaseConstructor<SheSafeDatabase> {
    /**
     * Instantiates the platform-specific generated implementation of [SheSafeDatabase].
     *
     * @return A newly initialized [SheSafeDatabase] instance.
     */
    override fun initialize(): SheSafeDatabase
}

/**
 * Creates a platform-specific [RoomDatabase.Builder] configured with the appropriate file path for [SheSafeDatabase].
 *
 * @return A [RoomDatabase.Builder] ready to be finalized via [buildSheSafeDatabase].
 */
expect fun createDatabaseBuilder(): RoomDatabase.Builder<SheSafeDatabase>

/**
 * Configures common SQLite driver and coroutine dispatcher settings on [builder] and builds [SheSafeDatabase].
 *
 * @param builder The platform-specific [RoomDatabase.Builder] for [SheSafeDatabase].
 * @return A fully configured [SheSafeDatabase] instance backed by [BundledSQLiteDriver].
 */
fun buildSheSafeDatabase(builder: RoomDatabase.Builder<SheSafeDatabase>): SheSafeDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()

/**
 * File name used for storing the local SQLite database on device storage.
 */
const val SHESAFE_DB_FILE_NAME = "shesafe.db"
