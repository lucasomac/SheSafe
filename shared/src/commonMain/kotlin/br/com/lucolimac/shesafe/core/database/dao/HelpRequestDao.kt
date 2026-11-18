package br.com.lucolimac.shesafe.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import br.com.lucolimac.shesafe.core.database.entity.HelpRequestEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object for querying and mutating [HelpRequestEntity] rows in the `help_requests` table.
 */
@Dao
interface HelpRequestDao {
    /**
     * Observes all help requests belonging to [userEmail], ordered from newest to oldest.
     *
     * @param userEmail Email address of the authenticated user owning the help requests.
     * @return A cold [Flow] emitting the user's ordered list of [HelpRequestEntity] whenever the table changes.
     */
    @Query("SELECT * FROM help_requests WHERE user_email = :userEmail ORDER BY created_at_epoch_ms DESC")
    fun observeByUser(userEmail: String): Flow<List<HelpRequestEntity>>

    /**
     * Inserts or updates a single [HelpRequestEntity] record in the local database.
     *
     * @param entity The help request entity to insert or update.
     */
    @Upsert
    suspend fun upsert(entity: HelpRequestEntity)

    /**
     * Inserts a batch of [HelpRequestEntity] records, replacing any conflicting primary keys.
     *
     * @param entities The collection of help request entities to persist.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<HelpRequestEntity>)

    /**
     * Deletes all cached help requests associated with [userEmail].
     *
     * @param userEmail Email address of the user whose help requests should be removed.
     */
    @Query("DELETE FROM help_requests WHERE user_email = :userEmail")
    suspend fun deleteAllForUser(userEmail: String)

    /**
     * Atomically replaces all cached help requests for [userEmail] with [entities].
     *
     * @param userEmail Email address of the user whose local cache is being synchronized.
     * @param entities Complete replacement list of [HelpRequestEntity] records for the user.
     */
    @Transaction
    suspend fun replaceAllForUser(userEmail: String, entities: List<HelpRequestEntity>) {
        deleteAllForUser(userEmail)
        if (entities.isNotEmpty()) {
            insertAll(entities)
        }
    }
}
