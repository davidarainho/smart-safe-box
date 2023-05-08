package com.example.es_database

import android.content.Context
// import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.es_database.lock.Lock
import com.example.es_database.lock.LockDao
import com.example.es_database.lock.LockDatabase
import junit.framework.TestCase.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.*
import androidx.lifecycle.Observer
import junit.framework.TestCase
import kotlinx.coroutines.flow.collect

class LockTest {
    private lateinit var lockDao: LockDao
    private lateinit var lockDatabase: LockDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        lockDatabase = Room.inMemoryDatabaseBuilder(context, LockDatabase::class.java).build()
        lockDao = lockDatabase.dao
    }

    @After
    fun cleanup() {
        lockDatabase.close()
    }

    @Test
    fun deleteLock() = runBlocking {
        val lock = Lock(lock_id = 1, lock_name = "Lock 1", number_of_users = 5, last_access = "2023-05-08", user_last_access = "Logan")
        lockDao.upsertLock(lock)

        val retrievedLock = lockDao.getLockByLockId(lock.lock_id).firstOrNull()
        assertNotNull(retrievedLock)

        lockDao.deleteLock(retrievedLock!!)

        val locks = lockDao.getLockByLockId(lock.lock_id).firstOrNull()
        assertNull(locks)
    }


    @Test
    fun getLockByLockId() = runBlocking {
        val lock = Lock(
            lock_id = 1,
            lock_name = "Lock 1",
            number_of_users = 5,
            last_access = "2023-05-08",
            user_last_access = "Logan"
        )
        lockDao.upsertLock(lock)

        val retrievedLock = lockDao.getLockByLockId(lock.lock_id).firstOrNull()

        assertNotNull(retrievedLock)
        assertEquals(lock.lock_id, retrievedLock?.lock_id)
        assertEquals(lock.lock_name, retrievedLock?.lock_name)
        assertEquals(lock.number_of_users, retrievedLock?.number_of_users)
    }


    @Test
    fun orderLocksByUserNumber() = runBlocking {
        val lock1 = Lock(
            lock_id = 1,
            lock_name = "Lock 1",
            number_of_users = 5,
            last_access = "2023-05-08",
            user_last_access = "Logan"
        )
        val lock2 = Lock(
            lock_id = 2,
            lock_name = "Lock 2",
            number_of_users = 3,
            last_access = "2023-05-08",
            user_last_access = "Logan"
        )
        val lock3 = Lock(
            lock_id = 3,
            lock_name = "Lock 3",
            number_of_users = 8,
            last_access = "2023-05-08",
            user_last_access = "Logan"
        )

        lockDao.upsertLock(lock1)
        lockDao.upsertLock(lock2)
        lockDao.upsertLock(lock3)

        val locks = lockDao.orderLocksByUserNumber().first()

        assertEquals(3, locks.size)
        assertEquals("Lock 3", locks[0].lock_name)
        assertEquals("Lock 1", locks[1].lock_name)
        assertEquals("Lock 2", locks[2].lock_name)
    }

    @Test
    fun orderLocksByLastAccess() = runBlocking {
        // Insert some locks with different last access timestamps
        val lock1 = Lock(
            lock_id = 1,
            lock_name = "Lock 1",
            last_access = "2023-05-17",
            user_last_access = "Logan",
            number_of_users = 5
        )
        val lock2 = Lock(
            lock_id = 2,
            lock_name = "Lock 2",
            last_access = "2023-05-06",
            user_last_access = "Logan",
            number_of_users = 5
        )
        val lock3 = Lock(
            lock_id = 3,
            lock_name = "Lock 3",
            last_access = "2023-05-08",
            user_last_access = "Logan",
            number_of_users = 5
        )

        lockDao.upsertLock(lock1)
        lockDao.upsertLock(lock2)
        lockDao.upsertLock(lock3)

        val locks = lockDao.orderLocksByLastAccess().first()

        assertEquals(3, locks.size)
        assertEquals("Lock 1", locks[0].lock_name)
        assertEquals("Lock 3", locks[1].lock_name)
        assertEquals("Lock 2", locks[2].lock_name)
    }

    @Test
    fun getLockByLockName() = runBlocking {
        val lock = Lock(lock_id = 1, lock_name = "Lock 1", number_of_users = 5, last_access = "2023-05-08", user_last_access = "Logan")
        lockDao.upsertLock(lock)

        val retrievedLock = lockDao.getLockByLockName(lock.lock_name).firstOrNull()

        assertNotNull(retrievedLock)
        assertEquals(lock.lock_id, retrievedLock?.lock_id)
        assertEquals(lock.lock_name, retrievedLock?.lock_name)
        assertEquals(lock.number_of_users, retrievedLock?.number_of_users)
    }

    @Test
    fun updateLockName() = runBlocking {
        val lock = Lock(lock_id = 1, lock_name = "Lock 1", number_of_users = 5, last_access = "2023-05-08", user_last_access = "Logan")
        lockDao.upsertLock(lock)

        val newLockName = "Updated Lock"
        lockDao.updateLockName(lock.lock_id, newLockName)

        val retrievedLock = lockDao.getLockByLockId(lock.lock_id).firstOrNull()

        assertNotNull(retrievedLock)
        assertEquals(newLockName, retrievedLock?.lock_name)
    }

    @Test
    fun getLockIdByName() = runBlocking {
        val lock = Lock(lock_id = 1, lock_name = "Lock 1", number_of_users = 5, last_access = "2023-05-08", user_last_access = "Logan")
        lockDao.upsertLock(lock)

        val retrievedLockId = lockDao.getLockIdByName(lock.lock_name)

        assertEquals(lock.lock_id, retrievedLockId)
    }


}