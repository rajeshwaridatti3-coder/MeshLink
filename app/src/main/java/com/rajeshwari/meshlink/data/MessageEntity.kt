package com.rajeshwari.meshlink.data
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "messages")
data class MessageEntity(
 @PrimaryKey val id: String, val senderLabel: String, val recipientId: String,
 val encryptedPayload: String, val preview: String, val createdAt: Long, val expiresAt: Long,
 val hops: Int, val maxHops: Int, val route: String, val delivered: Boolean
)
