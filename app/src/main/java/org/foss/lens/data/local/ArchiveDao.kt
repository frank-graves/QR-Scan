// org/foss/lens/data/local/ArchiveDao.kt
package org.foss.lens.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArchiveDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ArchiveEntity): Long

    @Query("SELECT * FROM archive ORDER BY timestamp DESC")
    suspend fun getAll(): List<ArchiveEntity>

    @Query("DELETE FROM archive WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM archive")
    suspend fun clear()
}