package com.example.es_database

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.es_database.user.UserDao
import com.example.es_database.user.UserDatabase
import org.junit.runner.RunWith
// import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.es_database.user.User
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.*
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class UserTest {

    private lateinit var userDao: UserDao
    private lateinit var db: UserDatabase

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, UserDatabase::class.java).build()
        userDao = db.dao
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndRetrieveUserByUsername() = runBlocking {
        val user = User("JohnDoe", "john@example.com", "password")
        userDao.upsertUser(user)

        val users = userDao.getUserByUsername().first()

        Assert.assertEquals(1, users.size)
        Assert.assertEquals("JohnDoe", users[0].username)
        Assert.assertEquals("john@example.com", users[0].email)
        Assert.assertEquals("password", users[0].password)
        //Assert.assertEquals(1, users[0].allow_notifications)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndRetrieveUserByEmail() = runBlocking {
        val user = User("JaneSmith", "jane@example.com", "password")
        userDao.upsertUser(user)

        val users = userDao.getUserByEmail().first()

        Assert.assertEquals(1, users.size)
        Assert.assertEquals("JaneSmith", users[0].username)
        Assert.assertEquals("jane@example.com", users[0].email)
        Assert.assertEquals("password", users[0].password)
        //Assert.assertEquals(1, users[0].allow_notifications)
    }

    @Test
    fun deleteUser() = runBlocking {
        val user = User("JohnDoe", "john@example.com", "password")
        userDao.upsertUser(user)

        val retrievedUser = userDao.getUserByUsername().first().firstOrNull()
        assertNotNull(retrievedUser)

        userDao.deleteUser(retrievedUser!!)

        val users = userDao.getUserByUsername().first()
        assertEquals(0, users.size)
    }

    @Test
    fun getUserByUserId() = runBlocking {
        val user = User("JohnDoe", "john@example.com", "password",47)
        userDao.upsertUser(user)

        val retrievedUser = userDao.getUserByUserId(user.user_id).firstOrNull()

        assertNotNull(retrievedUser)
        assertEquals(user.user_id, retrievedUser?.user_id)
        assertEquals(user.username, retrievedUser?.username)
        assertEquals(user.email, retrievedUser?.email)
        assertEquals(user.password, retrievedUser?.password)
    }

    @Test
    fun getUserIdByEmail() = runBlocking {
        val user = User("JohnDoe", "john@example.com", "password",1)
        userDao.upsertUser(user)

        val retrievedUserId = userDao.getUserIdByEmail(user.email)

        assertEquals(user.user_id, retrievedUserId)
    }


    @Test
    fun updateUsername() = runBlocking {
        val user = User("JohnDoe", "john@example.com", "password",7)
        userDao.upsertUser(user)

        val newUsername = "JohnSmith"
        userDao.updateUsername(user.user_id, newUsername)

        val retrievedUser = userDao.getUserByUserId(user.user_id).first()

        assertEquals(newUsername, retrievedUser.username)
    }

    @Test
    fun updatePassword() = runBlocking {
        val user = User("JohnDoe", "john@example.com", "password",9)
        userDao.upsertUser(user)

        val newPassword = "newPassword"
        userDao.updatePassword(user.user_id, newPassword)

        val retrievedUser = userDao.getUserByUserId(user.user_id).first()

        assertEquals(newPassword, retrievedUser.password)
    }

    @Test
    fun updateEmail() = runBlocking {
        val user = User("JohnDoe", "john@example.com", "password",4)
        userDao.upsertUser(user)

        val newEmail = "john.smith@example.com"
        userDao.updateEmail(user.user_id, newEmail)

        val retrievedUser = userDao.getUserByUserId(user.user_id).firstOrNull()

        assertNotNull(retrievedUser)
        assertEquals(newEmail, retrievedUser?.email)
    }

}








