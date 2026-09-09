// org/foss/lens/data/local/LensDatabase.kt
package org.foss.lens.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Base local única: historial de escaneos genéricos + inventario de activos.
 *
 * Migración destructiva a propósito mientras esto es v2 en desarrollo: los
 * registros de una versión de clase no son sagrados. Cuando haya usuarios de
 * verdad se escribe una migración real.
 */
@Database(
    entities = [ScanHistoryEntity::class, AssetEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LensDatabase : RoomDatabase() {

    abstract fun scanHistoryDao(): ScanHistoryDao
    abstract fun assetDao(): AssetDao

    companion object {
        @Volatile
        private var instance: LensDatabase? = null

        fun getInstance(context: Context): LensDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    LensDatabase::class.java,
                    "lens.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
    }
}
