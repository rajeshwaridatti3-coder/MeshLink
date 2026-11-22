package com.rajeshwari.meshlink.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [MessageEntity::class], version = 1, exportSchema = false)
abstract class MeshDatabase : RoomDatabase() {
 abstract fun messageDao(): MessageDao
 companion object {
  @Volatile private var instance: MeshDatabase? = null
  fun get(context: Context): MeshDatabase = instance ?: synchronized(this) {
   instance ?: Room.databaseBuilder(context.applicationContext, MeshDatabase::class.java, "meshlink.db").build().also { instance = it }
  }
 }
}
