package org.foss.lens.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Instant

@RunWith(RobolectricTestRunner::class)
class ArchiveDaoTest {
    private lateinit var db: ArchiveDatabase
    private lateinit var dao: ArchiveDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, ArchiveDatabase::class.java).build()
        dao = db.archiveDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndRetrieve() = runBlockingTest {
        val entity = ArchiveEntity(payload = "test", format = "QR", timestamp = Instant.now())
        val id = dao.insert(entity)
        assertTrue(id > 0)

        val all = dao.getAll()
        assertEquals(1, all.size)
        assertEquals("test", all[0].payload)
    }

    @Test
    fun deleteWorks() = runBlockingTest {
        val entity = ArchiveEntity(payload = "delete-me", format = "QR", timestamp = Instant.now())
        val id = dao.insert(entity)
        dao.delete(id)

        val all = dao.getAll()
        assertTrue(all.isEmpty())
    }

    @Test
    fun clearAll() = runBlockingTest {
        val e1 = ArchiveEntity(payload = "a", format = "QR", timestamp = Instant.now())
        val e2 = ArchiveEntity(payload = "b", format = "QR", timestamp = Instant.now())
        dao.insert(e1)
        dao.insert(e2)
        dao.clear()

        val all = dao.getAll()
        assertTrue(all.isEmpty())
    }

    // Additional test: descending timestamp order
    @Test
    fun orderByTimestampDesc() = runBlockingTest {
        val now = Instant.now()
        val e1 = ArchiveEntity(payload = "first", format = "QR", timestamp = now.minusSeconds(10))
        val e2 = ArchiveEntity(payload = "second", format = "QR", timestamp = now)
        dao.insert(e1)
        dao.insert(e2)

        val all = dao.getAll()
        assertEquals(2, all.size)
        assertEquals("second", all[0].payload)
        assertEquals("first", all[1].payload)
    }

    private fun runBlockingTest(block: suspend () -> Unit) = kotlinx.coroutines.runBlocking { block() }
}
