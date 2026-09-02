package org.foss.lens.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.foss.lens.data.local.ArchiveDatabase
import org.foss.lens.domain.Codex
import java.time.Instant
import kotlinx.coroutines.runBlocking

@RunWith(RobolectricTestRunner::class)
class ArchiveLocalTest {
    private lateinit var archiveLocal: ArchiveLocal
    private lateinit var db: ArchiveDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, ArchiveDatabase::class.java).build()
        archiveLocal = ArchiveLocal(db)
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun saveAndAll() = runBlocking {
        val codex = Codex(payload = "hello", format = "QR", timestamp = Instant.now())
        val id = archiveLocal.save(codex)
        assertTrue(id > 0)

        val all = archiveLocal.all()
        assertEquals(1, all.size)
        assertEquals("hello", all[0].payload)
    }

    @Test
    fun deleteAndClear() = runBlocking {
        val codex = Codex(payload = "test", format = "QR", timestamp = Instant.now())
        val id = archiveLocal.save(codex)
        archiveLocal.delete(id)

        var all = archiveLocal.all()
        assertTrue(all.isEmpty())

        // Repopulate and clear everything
        archiveLocal.save(Codex(payload = "a", format = "QR", timestamp = Instant.now()))
        archiveLocal.save(Codex(payload = "b", format = "QR", timestamp = Instant.now()))
        archiveLocal.clear()
        all = archiveLocal.all()
        assertTrue(all.isEmpty())
    }

    private suspend fun runBlocking(block: suspend () -> Unit) = runBlocking { block() }
}
