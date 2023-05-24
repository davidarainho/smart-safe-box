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
import kotlinx.coroutines.test.runBlockingTest
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

        val user = User("johnDoe", "john@example.com", "password", allow_notifications=1)
        userDao.upsertUser(user)
        val userid= userDao.getUserIdByUsername(user.username)

        val lock = Lock("Front Door", "2023-05-08", "John Doe", 1,comment="old")
        lockDao.upsertLock(lock)
        val lockid= lockDao.getFirstLockId()

        val userLock = UserLock(userid, lockid, "1234", permission_level=1, 4)
        userLockDao.upsertUserLock(userLock)

        val retrievedUser = userDao.getUserByUserId(userid).first()
        val retrievedLock = lockDao.getLockByLockId(lockid).first()

        val expectedUser = user.copy(user_id = retrievedUser.user_id)
        val expectedLock = lock.copy(lock_id = retrievedLock.lock_id)
        assertEquals(expectedUser, retrievedUser)
        assertEquals(expectedLock, retrievedLock)
    }

    @Test
    fun testGetLocksByUserId() = runBlocking {

        val user = User("johnDoe", "john@example.com", "password", allow_notifications=1)
        userDao.upsertUser(user)
        val userId = userDao.getUserIdByUsername(user.username)

        val lock1 = Lock("Lock1", "2023-05-08", "Logan", 1,comment="old")
        val lock2 = Lock("Lock2", "2023-05-14", "John Doe", 2,comment="old")
        lockDao.upsertLock(lock1)
        lockDao.upsertLock(lock2)

        val lockId1 = lockDao.getLockIdByName(lock1.lock_name)
        val lockId2 = lockDao.getLockIdByName(lock2.lock_name)
        val userLock1 = UserLock(userId, lockId1, "1234", permission_level=1, 5)
        val userLock2 = UserLock(userId, lockId2, "7788", permission_level=1, 7)
        userLockDao.upsertUserLock(userLock1)
        userLockDao.upsertUserLock(userLock2)

        val locksByUserId = userLockDao.getLocksByUserId(userId)

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

        val user1 = User("johnDoe", "john@example.com", "password", allow_notifications=1)
        val user2 = User("janeSmith", "jane@example.com", "password", allow_notifications=1)
        userDao.upsertUser(user1)
        userDao.upsertUser(user2)

        val lock1 = Lock("Lock1", "2023-05-08", "John Doe", 1,comment="old")
        val lock2 = Lock("Lock2", "2023-05-08", "John Doe", 2,comment="old")
        lockDao.upsertLock(lock1)
        lockDao.upsertLock(lock2)

        val lock3 = Lock("Lock3", "2023-05-08", "Jane Smith", 3,comment="old")
        val lock4 = Lock("Lock4", "2023-05-08", "Jane Smith", 4,comment="old")
        lockDao.upsertLock(lock3)
        lockDao.upsertLock(lock4)

        val userId1 = userDao.getUserIdByUsername(user1.username)
        val userId2 = userDao.getUserIdByUsername(user2.username)

        val userLock1 = UserLock(userId1, lockDao.getLockIdByName(lock1.lock_name), "1111", permission_level=1, 5)
        val userLock2 = UserLock(userId1, lockDao.getLockIdByName(lock2.lock_name), "2582", permission_level=1, 8)
        val userLock3 = UserLock(userId2, lockDao.getLockIdByName(lock3.lock_name), "1473", permission_level=1, 15)
        val userLock4 = UserLock(userId2, lockDao.getLockIdByName(lock4.lock_name), "6987", permission_level=1, 17)

        userLockDao.upsertUserLock(userLock1)
        userLockDao.upsertUserLock(userLock2)
        userLockDao.upsertUserLock(userLock3)
        userLockDao.upsertUserLock(userLock4)

        val locksByUserId1 = userLockDao.getLocksByUserId(userId1)
        val expectedLock1 = lock1.copy(lock_id = locksByUserId1[0].lock_id)
        val expectedLock2 = lock2.copy(lock_id = locksByUserId1[1].lock_id)

        assertEquals(2, locksByUserId1.size)
        assertEquals(expectedLock1, locksByUserId1[0])
        assertEquals(expectedLock2, locksByUserId1[1])

        val locksByUserId2 = userLockDao.getLocksByUserId(userId2)
        val expectedLock3 = lock3.copy(lock_id = locksByUserId2[0].lock_id)
        val expectedLock4 = lock4.copy(lock_id = locksByUserId2[1].lock_id)

        assertEquals(2, locksByUserId2.size)
        assertEquals(expectedLock3, locksByUserId2[0])
        assertEquals(expectedLock4, locksByUserId2[1])
    }

    @Test
    fun getUsersByLockId() = runBlocking {
        val user1 = User("JohnDoe", "john@example.com", "password", allow_notifications=1,4)
        val user2 = User("JaneSmith", "jane@example.com", "password", allow_notifications=1,7)
        userDao.upsertUser(user1)
        userDao.upsertUser(user2)

        val lock = Lock(lock_id = 1, lock_name = "Lock 1", number_of_users = 2, last_access = "2023-05-08", user_last_access = "Logan",comment="old")
        lockDao.upsertLock(lock)

        val userLock1 = UserLock(user_id = user1.user_id, lock_id = lock.lock_id, lock_access_pin = "1234", permission_level=1,userLockId=1)
        val userLock2 = UserLock(user_id = user2.user_id, lock_id = lock.lock_id, lock_access_pin = "5678", permission_level=1,userLockId=2)
        userLockDao.upsertUserLock(userLock1)
        userLockDao.upsertUserLock(userLock2)

        val users = userLockDao.getUsersByLockId(lock.lock_id)

        assertEquals(2, users.size)
        assertTrue(users.contains(user1))
        assertTrue(users.contains(user2))
    }

    @Test
    fun updateLockPin() = runBlocking {
        val user = User("JohnDoe", "john@example.com", "password", allow_notifications=1,99)
        userDao.upsertUser(user)

        val lock = Lock(lock_id = 1, lock_name = "Lock 1", number_of_users = 1, last_access = "2023-05-08", user_last_access = "Logan",comment="old")
        lockDao.upsertLock(lock)

        val userLock = UserLock(user_id = user.user_id, lock_id = lock.lock_id, lock_access_pin = "1234", permission_level=1, userLockId = 7)
        userLockDao.upsertUserLock(userLock)

        val newLockPin = "5678"
        userLockDao.updateLockPin(user.user_id, lock.lock_id, newLockPin)

        val updatedUserLock = userLockDao.getAllUserLocksWithRelations().firstOrNull { it.userLockId == userLock.userLockId }

        assertNotNull(updatedUserLock)
        assertEquals(newLockPin, updatedUserLock?.lock_access_pin)
    }

    @Test
    fun testGetUserLockPermissionLevel() = runBlocking {

        val user = User("johnDoe", "john@example.com", "password", allow_notifications = 1)
        userDao.upsertUser(user)
        val userId = userDao.getUserIdByUsername(user.username)

        val lock = Lock("Front Door", "2023-05-08", "John Doe", 1, comment = "old")
        lockDao.upsertLock(lock)
        val lockId = lockDao.getFirstLockId()

        val userLock = UserLock(userId, lockId, "1234", permission_level = 1)
        userLockDao.upsertUserLock(userLock)

        val permissionLevel = userLockDao.getUserLockPermissionLevel(userId, lockId)

        assertEquals(userLock.permission_level, permissionLevel)
    }

    @Test
    fun testUpdatePermissionLevel() = runBlocking {

        val user = User("johnDoe", "john@example.com", "password", allow_notifications=1)
        userDao.upsertUser(user)
        val userId = userDao.getUserIdByUsername(user.username)

        val lock = Lock("Front Door", "2023-05-08", "John Doe", 1,comment="old")
        lockDao.upsertLock(lock)
        val lockId = lockDao.getFirstLockId()

        val userLock = UserLock(userId, lockId, "1234", permission_level=1, 4)
        userLockDao.upsertUserLock(userLock)

        val newPermissionLevel = 2
        userLockDao.updatePermissionLevel(userId, lockId, newPermissionLevel)

        val updatedUserLock = userLockDao.getUserLockPermissionLevel(userId, lockId)

        assertEquals(newPermissionLevel, updatedUserLock)
    }

    @Test
    fun testGetPermissionLevelsByLockId() = runBlocking {

        val user1 = User("johnDoe", "john@example.com", "password", allow_notifications = 1)
        val user2 = User("janeDoe", "jane@example.com", "password", allow_notifications = 0)
        userDao.upsertUser(user1)
        userDao.upsertUser(user2)
        val userId1 = userDao.getUserIdByUsername(user1.username)
        val userId2 = userDao.getUserIdByUsername(user2.username)

        val lock = Lock("Front Door", "2023-05-08", "John Doe", 1, comment = "old")
        lockDao.upsertLock(lock)
        val lockId = lockDao.getFirstLockId()

        val userLock1 = UserLock(userId1, lockId, "1234", permission_level = 1)
        val userLock2 = UserLock(userId2, lockId, "5678", permission_level = 2)

        userLockDao.upsertUserLock(userLock1)
        userLockDao.upsertUserLock(userLock2)

        val permissionLevels = userLockDao.getPermissionLevelsByLockId(lockId)

        assertEquals(1, permissionLevels[userId1])
        assertEquals(2, permissionLevels[userId2])
    }

    @Test
    fun testGetPermissionLevelsByUserId() = runBlocking {

        val user = User("johnDoe", "john@example.com", "password", allow_notifications = 1)
        userDao.upsertUser(user)
        val userId = userDao.getUserIdByUsername(user.username)

        val lock1 = Lock("Front Door", "2023-05-08", "John Doe", 1, comment = "old")
        val lock2 = Lock("Back Door", "2023-05-09", "Jane Doe", 2, comment = "new")
        lockDao.upsertLock(lock1)
        lockDao.upsertLock(lock2)
        val lockId1 = lockDao.getFirstLockId()
        val lockId2 = lockDao.getLastLockId()

        val userLock1 = UserLock(userId, lockId1, "1234", permission_level = 1)
        val userLock2 = UserLock(userId, lockId2, "5678", permission_level = 2)
        userLockDao.upsertUserLock(userLock1)
        userLockDao.upsertUserLock(userLock2)

        val permissionLevels = userLockDao.getPermissionLevelsByUserId(userId)

        assertEquals(1, permissionLevels[lockId1])
        assertEquals(2, permissionLevels[lockId2])
    }

    @Test
    fun testGetUsersByPermissionLevel() = runBlocking {
        val user1 = User("johnDoe", "john@example.com", "password", 1)
        val user2 = User("janeDoe", "jane@example.com", "password", 1)
        userDao.upsertUser(user1)
        userDao.upsertUser(user2)

        val userId1 = userDao.getUserIdByUsername(user1.username)
        val userId2 = userDao.getUserIdByUsername(user2.username)

        val lock = Lock("Front Door", "2023-05-08", "John Doe", 1, "old")
        lockDao.upsertLock(lock)
        val lockId = lockDao.getLastLockId()

        val userLock1 = UserLock(userId1, lockId, "1234", 1)
        val userLock2 = UserLock(userId2, lockId, "5678", 2)
        userLockDao.upsertUserLock(userLock1)
        userLockDao.upsertUserLock(userLock2)

        val usersWithPermissionLevel1 = userLockDao.getUsersByPermissionLevel(lockId, 1)
        val usersWithPermissionLevel2 = userLockDao.getUsersByPermissionLevel(lockId, 2)

        assertEquals(1, usersWithPermissionLevel1.size)
        assertEquals(1, usersWithPermissionLevel2.size)

        assertEquals(user1.copy(user_id = userId1), usersWithPermissionLevel1[0])
        assertEquals(user2.copy(user_id = userId2), usersWithPermissionLevel2[0])
    }

    @Test
    fun testGetLocksByPermissionLevel() = runBlocking {

        val user = User("johnDoe", "john@example.com", "password", allow_notifications = 1)
        userDao.upsertUser(user)
        val userId = userDao.getUserIdByUsername(user.username)

        val lock = Lock("Front Door", "2023-05-08", "John Doe", 1, comment = "old")
        lockDao.upsertLock(lock)
        val lockId = lockDao.getLastLockId()

        val lock2 = Lock(" Door", "2023-05-07", "Logan", 5, comment = "older", lock_id=99)
        lockDao.upsertLock(lock2)
        val lockId2 = lock2.lock_id

        val userLock = UserLock(userId, lockId, "1234", permission_level = 1)
        userLockDao.upsertUserLock(userLock)

        val userLock2 = UserLock(userId, lockId2, "1777", permission_level = 1)
        userLockDao.upsertUserLock(userLock2)

        val locks = userLockDao.getLocksByPermissionLevel(userId, 1)

        val expectedLock1 = Lock("Front Door", "2023-05-08", "John Doe", 1, comment = "old", lock_id = 1)
        val expectedLock2 = Lock(" Door", "2023-05-07", "Logan", 5, comment = "older", lock_id=99)
        assertEquals(expectedLock1, locks[0])
        assertEquals(expectedLock2, locks[1])
    }



}
