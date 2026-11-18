package br.com.lucolimac.shesafe.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a user's trusted emergency contact stored in the `secure_contacts` table.
 *
 * @property phoneNumber Normalized phone number serving as the primary key of the contact record.
 * @property name Display name of the trusted emergency contact.
 * @property userEmail Email address of the authenticated user who registered this contact.
 */
@Entity(tableName = "secure_contacts")
data class SecureContactEntity(
    @PrimaryKey
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "user_email")
    val userEmail: String,
)
