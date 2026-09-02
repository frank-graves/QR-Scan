// org/foss/lens/data/local/ArchiveDatabase.kt
package org.foss.lens.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context

@Database(entities = [ArchiveEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ArchiveDatabase : RoomDatabase() {
    abstract fun archiveDao(): ArchiveDao

    companion object {
        @Volatile
        private var instance: ArchiveDatabase? = null

        fun getInstance(context: Context): ArchiveDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ArchiveDatabase::class.java,
                    "lens_archive.db"
                )
                // Destructive migration por diseño: el archivo es un historial local desechable,
                // no datos del usuario. Si v2 rompe el schema, se limpia y se regenera.
                .fallbackToDestructiveMigration()
                .build().also { instance = it }
            }
        }
    }
}
