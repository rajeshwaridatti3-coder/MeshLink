package com.rajeshwari.meshlink.security
import android.content.Context
import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
class PayloadCipher private constructor(private val key: SecretKey) {
 fun encrypt(text: String): String {
  val c = Cipher.getInstance("AES/GCM/NoPadding"); c.init(Cipher.ENCRYPT_MODE, key)
  return Base64.encodeToString(c.iv + c.doFinal(text.toByteArray(Charsets.UTF_8)), Base64.NO_WRAP)
 }
 fun decrypt(value: String): String {
  val bytes = Base64.decode(value, Base64.NO_WRAP); val iv = bytes.copyOfRange(0, 12); val data = bytes.copyOfRange(12, bytes.size)
  val c = Cipher.getInstance("AES/GCM/NoPadding"); c.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
  return c.doFinal(data).toString(Charsets.UTF_8)
 }
 companion object {
  private const val PREFS = "meshlink_crypto"; private const val KEY = "local_aes_key"
  fun getOrCreate(context: Context): PayloadCipher {
   val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
   val saved = prefs.getString(KEY, null)
   val secret = if (saved == null) KeyGenerator.getInstance("AES").apply { init(256) }.generateKey().also {
    prefs.edit().putString(KEY, Base64.encodeToString(it.encoded, Base64.NO_WRAP)).apply()
   } else SecretKeySpec(Base64.decode(saved, Base64.NO_WRAP), "AES")
   return PayloadCipher(secret)
  }
 }
}
