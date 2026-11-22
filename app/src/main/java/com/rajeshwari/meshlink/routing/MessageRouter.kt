package com.rajeshwari.meshlink.routing
import com.rajeshwari.meshlink.data.MessageDao
import com.rajeshwari.meshlink.data.MessageEntity
import com.rajeshwari.meshlink.security.PayloadCipher
import java.util.UUID
class MessageRouter(private val dao: MessageDao, private val cipher: PayloadCipher) {
 suspend fun createLocalMessage(body: String, recipientId: String, ttlMillis: Long = 86_400_000L): MessageEntity {
  val now = System.currentTimeMillis()
  return MessageEntity(UUID.randomUUID().toString(), "You", recipientId, cipher.encrypt(body), body, now,
   now + ttlMillis, 0, 5, "You", false)
 }
 suspend fun acceptForRelay(message: MessageEntity, peerId: String): Boolean {
  if (message.expiresAt <= System.currentTimeMillis() || message.hops >= message.maxHops) return false
  if (dao.findById(message.id) != null) return false
  dao.insert(message.copy(hops = message.hops + 1, route = "${message.route} → $peerId"))
  return true
 }
 suspend fun pruneExpired() = dao.deleteExpired(System.currentTimeMillis())
}
