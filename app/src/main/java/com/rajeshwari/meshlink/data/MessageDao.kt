package com.rajeshwari.meshlink.data
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
@Dao
interface MessageDao {
 @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insert(message: MessageEntity)
 @Query("SELECT * FROM messages ORDER BY createdAt DESC") fun observeAll(): Flow<List<MessageEntity>>
 @Query("SELECT * FROM messages WHERE id = :id LIMIT 1") suspend fun findById(id: String): MessageEntity?
 @Query("SELECT * FROM messages WHERE delivered = 0 AND expiresAt > :now AND hops < maxHops") suspend fun pendingForRelay(now: Long): List<MessageEntity>
 @Query("UPDATE messages SET hops = :hops, route = :route WHERE id = :id") suspend fun updateRelay(id: String, hops: Int, route: String)
 @Query("DELETE FROM messages WHERE expiresAt <= :now") suspend fun deleteExpired(now: Long)
}
