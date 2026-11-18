package br.com.lucolimac.shesafe.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import br.com.lucolimac.shesafe.core.database.entity.SecureContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SecureContactDao {
    @Query("SELECT * FROM secure_contacts WHERE user_email = :userEmail ORDER BY name ASC")
    fun observeByUser(userEmail: String): Flow<List<SecureContactEntity>>

    @Query("SELECT * FROM secure_contacts WHERE user_email = :userEmail ORDER BY name ASC")
    suspend fun getAllByUser(userEmail: String): List<SecureContactEntity>

    @Query("SELECT * FROM secure_contacts WHERE user_email = :userEmail AND phone_number = :phoneNumber LIMIT 1")
    suspend fun getByPhone(userEmail: String, phoneNumber: String): SecureContactEntity?

    @Upsert
    suspend fun upsert(entity: SecureContactEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<SecureContactEntity>)

    @Query("DELETE FROM secure_contacts WHERE user_email = :userEmail AND phone_number = :phoneNumber")
    suspend fun deleteByPhone(userEmail: String, phoneNumber: String)

    @Query("DELETE FROM secure_contacts WHERE user_email = :userEmail")
    suspend fun deleteAllForUser(userEmail: String)

    @Transaction
    suspend fun replaceAllForUser(userEmail: String, entities: List<SecureContactEntity>) {
        deleteAllForUser(userEmail)
        if (entities.isNotEmpty()) {
            insertAll(entities)
        }
    }
}
