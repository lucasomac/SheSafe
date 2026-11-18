package br.com.lucolimac.shesafe.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import br.com.lucolimac.shesafe.core.database.entity.HelpRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HelpRequestDao {
    @Query("SELECT * FROM help_requests WHERE user_email = :userEmail ORDER BY created_at_epoch_ms DESC")
    fun observeByUser(userEmail: String): Flow<List<HelpRequestEntity>>

    @Upsert
    suspend fun upsert(entity: HelpRequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<HelpRequestEntity>)

    @Query("DELETE FROM help_requests WHERE user_email = :userEmail")
    suspend fun deleteAllForUser(userEmail: String)

    @Transaction
    suspend fun replaceAllForUser(userEmail: String, entities: List<HelpRequestEntity>) {
        deleteAllForUser(userEmail)
        if (entities.isNotEmpty()) {
            insertAll(entities)
        }
    }
}
