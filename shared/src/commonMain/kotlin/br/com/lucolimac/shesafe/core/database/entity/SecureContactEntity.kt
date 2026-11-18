package br.com.lucolimac.shesafe.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
