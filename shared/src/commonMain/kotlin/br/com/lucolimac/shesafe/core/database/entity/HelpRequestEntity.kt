package br.com.lucolimac.shesafe.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a dispatched emergency help request stored in the `help_requests` table.
 *
 * @property id Unique identifier of the help request (typically matching its remote document ID or generated UUID).
 * @property userEmail Email address of the user who triggered the help request.
 * @property phoneNumbersCsv Comma-separated list of recipient phone numbers notified during the alert.
 * @property latitude Geographic latitude in decimal degrees captured at dispatch time, or `null` if unavailable.
 * @property longitude Geographic longitude in decimal degrees captured at dispatch time, or `null` if unavailable.
 * @property createdAtEpochMs Creation timestamp of the help request in UTC epoch milliseconds.
 */
@Entity(tableName = "help_requests")
data class HelpRequestEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "user_email")
    val userEmail: String,
    @ColumnInfo(name = "phone_numbers_csv")
    val phoneNumbersCsv: String,
    @ColumnInfo(name = "latitude")
    val latitude: Double?,
    @ColumnInfo(name = "longitude")
    val longitude: Double?,
    @ColumnInfo(name = "created_at_epoch_ms")
    val createdAtEpochMs: Long,
)
