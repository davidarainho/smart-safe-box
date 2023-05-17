package com.example.es_database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.es_database.lock.Lock
import com.example.es_database.lock.LockDao
import com.example.es_database.user.User
import com.example.es_database.user.UserDao
import com.example.es_database.userlock.UserLock
import com.example.es_database.userlock.UserLockDao
import com.example.es_database.userlock.UserLockDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserLockTest {

    private lateinit var userLockDao: UserLockDao
    private lateinit var lockDao: LockDao
    private lateinit var userDao: UserDao
    private lateinit var userLockDatabase: UserLockDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        userLockDatabase = Room.inMemoryDatabaseBuilder(context, UserLockDatabase::class.java).build()
        userLockDao = userLockDatabase.userLockDao()
        lockDao = userLockDatabase.lockDao()
        userDao = userLockDatabase.userDao()
    }

    @After
    fun tearDown() {
        userLockDatabase.close()
    }

    @Test
    fun addUserAndLock_AssociateUserLock() = runBlocking {
        // Create a user
        val user = User("johnDoe", "john@example.com", "password")
        userDao.upsertUser(user)
        val userid= userDao.getUserIdByUsername(user.username)

        // Create a lock
        val lock = Lock("Front Door", "2023-05-08", "John Doe", 1)
        lockDao.upsertLock(lock)
        val lockid= lockDao.getFirstLockId()

        // Create a user-lock association
        val userLock = UserLock(userid, lockid, "1234", 4)
        userLockDao.upsertUserLock(userLock)

        // Retrieve the associated user and lock
        val retrievedUser = userDao.getUserByUserId(userid).first()
        val retrievedLock = lockDao.getLockByLockId(lockid).first()

        val expectedUser = user.copy(user_id = retrievedUser.user_id)
        val expectedLock = lock.copy(lock_id = retrievedLock.lock_id)
        assertEquals(expectedUser, retrievedUser)
        assertEquals(expectedLock, retrievedLock)
    }

    @Test
    fun testGetLocksByUserId() = runBlocking {
        // Create a user
        val user = User("johnDoe", "john@example.com", "password")
        userDao.upsertUser(user)
        val userId = userDao.getUserIdByUsername(user.username)

        // Create locks associated with the user
        val lock1 = Lock("Lock1", "2023-05-08", "Logan", 1)
        val lock2 = Lock("Lock2", "2023-05-14", "John Doe", 2)
        lockDao.upsertLock(lock1)
        lockDao.upsertLock(lock2)

        // Create user-lock associations
        val lockId1 = lockDao.getLockIdByName(lock1.lock_name)
        val lockId2 = lockDao.getLockIdByName(lock2.lock_name)
        val userLock1 = UserLock(userId, lockId1, "1234", 5)
        val userLock2 = UserLock(userId, lockId2, "7788", 7)
        userLockDao.upsertUserLock(userLock1)
        userLockDao.upsertUserLock(userLock2)

        // Retrieve the locks by user ID
        val locksByUserId = userLockDao.getLocksByUserId(userId)

        // Assert the locks count and their details
        assertEquals(2, locksByUserId.size)

        val retrievedLock1 = locksByUserId[0]
        val expectedLock1 = lock1.copy(lock_id = retrievedLock1.lock_id)

        assertNotNull(retrievedLock1)
        assertEquals(expectedLock1, retrievedLock1)

        val retrievedLock2 = locksByUserId[1]
        val expectedLock2 = lock2.copy(lock_id = retrievedLock2.lock_id)
        assertNotNull(retrievedLock2)
        assertEquals(expectedLock2, retrievedLock2)
    }

    @Test
    fun testGetLocksByUserId_DifferentUsers() = runBlocking {
        // Create users
        val user1 = User("johnDoe", "john@example.com", "password")
        val user2 = User("janeSmith", "jane@example.com", "password")
        userDao.upsertUser(user1)
        userDao.upsertUser(user2)

        // Create locks associated with user1
        val lock1 = Lock("Lock1", "2023-05-08", "John Doe", 1)
        val lock2 = Lock("Lock2", "2023-05-08", "John Doe", 2)
        lockDao.upsertLock(lock1)
        lockDao.upsertLock(lock2)

        // Create locks associated with user2
        val lock3 = Lock("Lock3", "2023-05-08", "Jane Smith", 3)
        val lock4 = Lock("Lock4", "2023-05-08", "Jane Smith", 4)
        lockDao.upsertLock(lock3)
        lockDao.upsertLock(lock4)

        // Create user-lock associations
        val userId1 = userDao.getUserIdByUsername(user1.username)
        val userId2 = userDao.getUserIdByUsername(user2.username)

        val userLock1 = UserLock(userId1, lockDao.getLockIdByName(lock1.lock_name), "1111", 5)
        val userLock2 = UserLock(userId1, lockDao.getLockIdByName(lock2.lock_name), "2582", 8)
        val userLock3 = UserLock(userId2, lockDao.getLockIdByName(lock3.lock_name), "1473", 15)
        val userLock4 = UserLock(userId2, lockDao.getLockIdByName(lock4.lock_name), "6987", 17)

        userLockDao.upsertUserLock(userLock1)
        userLockDao.upsertUserLock(userLock2)
        userLockDao.upsertUserLock(userLock3)
        userLockDao.upsertUserLock(userLock4)

        // Retrieve locks for user1
        val locksByUserId1 = userLockDao.getLocksByUserId(userId1)
        val expectedLock1 = lock1.copy(lock_id = locksByUserId1[0].lock_id)
        val expectedLock2 = lock2.copy(lock_id = locksByUserId1[1].lock_id)

        // Assert the locks count and their details for user1
        assertEquals(2, locksByUserId1.size)
        assertEquals(expectedLock1, locksByUserId1[0])
        assertEquals(expectedLock2, locksByUserId1[1])

        // Retrieve locks for user2
        val locksByUserId2 = userLockDao.getLocksByUserId(userId2)
        val expectedLock3 = lock3.copy(lock_id = locksByUserId2[0].lock_id)
        val expectedLock4 = lock4.copy(lock_id = locksByUserId2[1].lock_id)


        // Assert the locks count and their details for user2
        assertEquals(2, locksByUserId2.size)
        assertEquals(expectedLock3, locksByUserId2[0])
        assertEquals(expectedLock4, locksByUserId2[1])
    }

    @Test
    fun getUsersByLockId() = runBlocking {
        val user1 = User("JohnDoe", "john@example.com", "password",4)
        val user2 = User("JaneSmith", "jane@example.com", "password",7)
        userDao.upsertUser(user1)
        userDao.upsertUser(user2)

        val lock = Lock(lock_id = 1, lock_name = "Lock 1", number_of_users = 2, last_access = "2023-05-08", user_last_access = "Logan")
        lockDao.upsertLock(lock)

        val userLock1 = UserLock(user_id = user1.user_id, lock_id = lock.lock_id, lock_access_pin = "1234",userLockId=1)
        val userLock2 = UserLock(user_id = user2.user_id, lock_id = lock.lock_id, lock_access_pin = "5678",userLockId=2)
        userLockDao.upsertUserLock(userLock1)
        userLockDao.upsertUserLock(userLock2)

        val users = userLockDao.getUsersByLockId(lock.lock_id)

        assertEquals(2, users.size)
        assertTrue(users.contains(user1))
        assertTrue(users.contains(user2))
    }

    @Test
    fun updateLockPin() = runBlocking {
        val user = User("JohnDoe", "john@example.com", "password",99)
        userDao.upsertUser(user)

        val lock = Lock(lock_id = 1, lock_name = "Lock 1", number_of_users = 1, last_access = "2023-05-08", user_last_access = "Logan")
        lockDao.upsertLock(lock)

        val userLock = UserLock(user_id = user.user_id, lock_id = lock.lock_id, lock_access_pin = "1234", userLockId = 7)
        userLockDao.upsertUserLock(userLock)

        val newLockPin = "5678"
        userLockDao.updateLockPin(user.user_id, lock.lock_id, newLockPin)

        val updatedUserLock = userLockDao.getAllUserLocksWithRelations().firstOrNull { it.userLockId == userLock.userLockId }

        assertNotNull(updatedUserLock)
        assertEquals(newLockPin, updatedUserLock?.lock_access_pin)
    }








}
