package br.com.lucolimac.shesafe.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import br.com.lucolimac.shesafe.core.database.entity.SecureContactEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object for querying and mutating [SecureContactEntity] rows in the `secure_contacts` table.
 */
@Dao
interface SecureContactDao {
    /**
     * Observes all trusted contacts registered for [userEmail], sorted alphabetically by contact name.
     *
     * @param userEmail Email address of the user owning the contacts.
     * @return A cold [Flow] emitting the user's sorted list of [SecureContactEntity] whenever the table changes.
     */
    @Query("SELECT * FROM secure_contacts WHERE user_email = :userEmail ORDER BY name ASC")
    fun observeByUser(userEmail: String): Flow<List<SecureContactEntity>>

    /**
     * Fetches a one-shot snapshot of all trusted contacts registered for [userEmail], sorted alphabetically by name.
     *
     * @param userEmail Email address of the user owning the contacts.
     * @return List of [SecureContactEntity] records associated with [userEmail].
     */
    @Query("SELECT * FROM secure_contacts WHERE user_email = :userEmail ORDER BY name ASC")
    suspend fun getAllByUser(userEmail: String): List<SecureContactEntity>

    /**
     * Looks up a single trusted contact by [userEmail] and [phoneNumber].
     *
     * @param userEmail Email address of the user owning the contact.
     * @param phoneNumber Phone number uniquely identifying the contact.
     * @return The matching [SecureContactEntity], or `null` if none exists.
     */
    @Query("SELECT * FROM secure_contacts WHERE user_email = :userEmail AND phone_number = :phoneNumber LIMIT 1")
    suspend fun getByPhone(userEmail: String, phoneNumber: String): SecureContactEntity?

    /**
     * Inserts or updates a single [SecureContactEntity] in the local database.
     *
     * @param entity The secure contact entity to insert or update.
     */
    @Upsert
    suspend fun upsert(entity: SecureContactEntity)

    /**
     * Inserts a batch of [SecureContactEntity] records, replacing any conflicting primary keys.
     *
     * @param entities The list of secure contact entities to persist.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<SecureContactEntity>)

    /**
     * Deletes a specific trusted contact matching [userEmail] and [phoneNumber].
     *
     * @param userEmail Email address of the user owning the contact.
     * @param phoneNumber Phone number of the contact to remove.
     */
    @Query("DELETE FROM secure_contacts WHERE user_email = :userEmail AND phone_number = :phoneNumber")
    suspend fun deleteByPhone(userEmail: String, phoneNumber: String)

    /**
     * Deletes all trusted contacts associated with [userEmail].
     *
     * @param userEmail Email address of the user whose contacts should be cleared.
     */
    @Query("DELETE FROM secure_contacts WHERE user_email = :userEmail")
    suspend fun deleteAllForUser(userEmail: String)

    /**
     * Atomically replaces all cached trusted contacts for [userEmail] with [entities].
     *
     * @param userEmail Email address of the user whose contact list is being synchronized.
     * @param entities Complete replacement list of [SecureContactEntity] records for the user.
     */
    @Transaction
    suspend fun replaceAllForUser(userEmail: String, entities: List<SecureContactEntity>) {
        deleteAllForUser(userEmail)
        if (entities.isNotEmpty()) {
            insertAll(entities)
        }
    }
}
