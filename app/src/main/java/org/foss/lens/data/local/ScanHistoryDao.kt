// org/foss/lens/data/local/ScanHistoryDao.kt
package org.foss.lens.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ScanHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ScanHistoryEntity): Long

    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC LIMIT 300")
    fun observeRecent(): kotlinx.coroutines.flow.Flow<List<ScanHistoryEntity>>

    @Query("DELETE FROM scan_history")
    suspend fun clear()
}
