// org/foss/lens/data/local/AssetDao.kt
package org.foss.lens.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.foss.lens.domain.SyncState

@Dao
interface AssetDao {

    @Query("SELECT * FROM assets ORDER BY createdLocalMs DESC")
    fun observeAll(): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE serial = :serial LIMIT 1")
    suspend fun bySerial(serial: String): AssetEntity?

    @Query("SELECT * FROM assets WHERE syncState = 'PENDING' ORDER BY createdLocalMs ASC")
    suspend fun pending(): List<AssetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: AssetEntity): Long

    @Query(
        "UPDATE assets SET syncState = :state, serverId = :serverId, errorMsg = :error " +
            "WHERE localId = :localId"
    )
    suspend fun markSync(
        localId: Long,
        state: SyncState,
        serverId: String?,
        error: String?
    )
}
