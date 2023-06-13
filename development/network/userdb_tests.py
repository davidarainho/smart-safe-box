import unittest
import datetime
from unittest.mock import patch
import sqlite3

# Import the module containing the functions
import userinfo

class TestFunctions(unittest.TestCase):
    
    @classmethod
    def setUpClass(cls):
        # Create an in-memory SQLite database for testing
        cls.conn = sqlite3.connect(':memory:')
        cls.cursor = cls.conn.cursor()
        cls.cursor.execute('''
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY,
                username TEXT,
                password_hash TEXT,
                email TEXT,
                access_level INTEGER
            )''')
        cls.cursor.execute('''
            CREATE TABLE IF NOT EXISTS locks (
                id INTEGER PRIMARY KEY,
                lock_id INTEGER,
                name TEXT,
                location TEXT,
                state INTEGER,
                max_users INTEGER
            )''')
        cls.cursor.execute('''
            CREATE TABLE IF NOT EXISTS lock_users (
                lock_id INTEGER,
                username TEXT,
                access_level INTEGER,
                start_time TIMESTAMP,
                end_time TIMESTAMP,
                FOREIGN KEY(lock_id) REFERENCES locks(id),
                FOREIGN KEY(username) REFERENCES users(id)
            )''')
        cls.cursor.execute('''
            CREATE TABLE IF NOT EXISTS password_reset_tokens (
                token TEXT PRIMARY KEY,
                username TEXT,
                FOREIGN KEY(username) REFERENCES users(id)
            )''')
        cls.conn.commit()

    @classmethod
    def tearDownClass(cls):
        # Close the database connection
        cls.cursor.close()
        cls.conn.close()

    def setUp(self):
        # Clear the tables before each test
        self.cursor.execute("DELETE FROM users")
        self.cursor.execute("DELETE FROM locks")
        self.cursor.execute("DELETE FROM lock_users")
        self.cursor.execute("DELETE FROM password_reset_tokens")
        self.conn.commit()

    def test_add_user(self):
        # Test adding a user to the database
        userinfo.add_user('user1', 'password1', 'user1@example.com', 1, [(1, 2)], start_time='2023-01-01', end_time='2023-01-02')
        self.cursor.execute("SELECT * FROM users WHERE username='user1'")
        result = self.cursor.fetchone()
        self.assertIsNotNone(result)
        self.assertEqual(result[1], 'user1')
        # ... add more assertions as needed

    def test_allocate_lock(self):
        # Test allocating a lock to a user
        userinfo.allocate_lock('user1', 1, 2, start_time='2023-01-01', end_time='2023-01-02')
        self.cursor.execute("SELECT * FROM lock_users WHERE username='user1' AND lock_id=1")
        result = self.cursor.fetchone()
        self.assertIsNotNone(result)
        self.assertEqual(result[0], 1)
        self.assertEqual(result[1], 'user1')
        # ... add more assertions as needed

    def test_deallocate_lock(self):
        # Test deallocating a lock from a user
        self.cursor.execute("INSERT INTO lock_users (lock_id, username, access_level) VALUES (?, ?, ?)", (1, 'user1', 2))
        self.conn.commit()
        userinfo.deallocate_lock('user1', 1)
        self.cursor.execute("SELECT * FROM lock_users WHERE username='user1' AND lock_id=1")
        result = self.cursor.fetchone()
        self.assertIsNone(result)
        # ... add more assertions as needed

    def test_remove_user(self):
        # Test removing a user from the database
        self.cursor.execute("INSERT INTO users (id, username, password_hash, email, access_level) VALUES (?, ?, ?, ?, ?)",
                            (1, 'user1', 'password1', 'user1@example.com', 1))
        self.conn.commit()
        userinfo.remove_user('user1')
        self.cursor.execute("SELECT * FROM users WHERE username='user1'")
        result = self.cursor.fetchone()
        self.assertIsNone(result)
        # ... add more assertions as needed

    def test_remove_lock(self):
        # Test removing a lock from the database
        self.cursor.execute("INSERT INTO locks (id, lock_id, name, location, state, max_users) VALUES (?, ?, ?, ?, ?, ?)",
                            (1, 123, 'Lock 1', 'Location 1', 1, 2))
        self.conn.commit()
        userinfo.remove_lock(123)
        self.cursor.execute("SELECT * FROM locks WHERE lock_id=123")
        result = self.cursor.fetchone()
        self.assertIsNone(result)
        # ... add more assertions as needed

    def test_get_users_for_lock(self):
        # Test retrieving users associated with a lock
        self.cursor.execute("INSERT INTO locks (id, lock_id, name, location, state, max_users) VALUES (?, ?, ?, ?, ?, ?)",
                            (1, 123, 'Lock 1', 'Location 1', 1, 2))
        self.cursor.execute("INSERT INTO users (id, username, password_hash, email, access_level) VALUES (?, ?, ?, ?, ?)",
                            (1, 'user1', 'password1', 'user1@example.com', 1))
        self.cursor.execute("INSERT INTO lock_users (lock_id, username, access_level) VALUES (?, ?, ?)",
                            (123, 'user1', 2))
        self.conn.commit()
        result = userinfo.get_users_for_lock(123)
        self.assertEqual(result, ['user1'])
        # ... add more assertions as needed

    def test_get_locks_for_user(self):
        # Test retrieving locks associated with a user
        self.cursor.execute("INSERT INTO locks (id, lock_id, name, location, state, max_users) VALUES (?, ?, ?, ?, ?, ?)",
                            (1, 123, 'Lock 1', 'Location 1', 1, 2))
        self.cursor.execute("INSERT INTO users (id, username, password_hash, email, access_level) VALUES (?, ?, ?, ?, ?)",
                            (1, 'user1', 'password1', 'user1@example.com', 1))
        self.cursor.execute("INSERT INTO lock_users (lock_id, username, access_level) VALUES (?, ?, ?)",
                            (123, 'user1', 2))
        self.conn.commit()
        result = userinfo.get_locks_for_user('user1')
        self.assertEqual(result, [123])
        # ... add more assertions as needed

    def test_get_lock_state(self):
        # Test getting the state of a lock
        self.cursor.execute("INSERT INTO locks (id, lock_id, name, location, state, max_users) VALUES (?, ?, ?, ?, ?, ?)",
                            (1, 123, 'Lock 1', 'Location 1', 1, 2))
        self.conn.commit()
        result = userinfo.get_lock_state(123)
        self.assertEqual(result, 1)
        # ... add more assertions as needed

    def test_update_username_function(self):
        # Test the update_username function
        userinfo.add_user('user1', 'password1', 'user1@example.com', 1, [])
        new_username = userinfo.update_username_function('user1', 'new_user1')
        self.assertEqual(new_username, 'new_user1')
        self.cursor.execute("SELECT * FROM users WHERE username='new_user1'")
        result = self.cursor.fetchone()
        self.assertIsNotNone(result)
        self.assertEqual(result[1], 'new_user1')
        # ... add more assertions as needed

    def test_update_password_function(self):
        # Test updating the password for a user
        update_password = userinfo.update_password_function('user1')
        new_password = update_password('new_password1')
        self.assertEqual(new_password, 'new_password1')
        # ... add more assertions as needed

    def test_update_email_function(self):
        # Test updating the email for a user
        update_email = userinfo.update_email_function('user1')
        new_email = update_email('new_user1@example.com')
        self.assertEqual(new_email, 'new_user1@example.com')
        # ... add more assertions as needed

    def test_send_email(self):
        # Test sending an email
        with patch('userinfo.send_email') as mock_send_email:
            userinfo.send_email('Subject', 'Body', 'recipient@example.com')
            mock_send_email.assert_called_with('Subject', 'Body', 'recipient@example.com')

    def test_generate_reset_token(self):
        # Test generating a password reset token
        self.cursor.execute("INSERT INTO users (id, username, password_hash, email, access_level) VALUES (?, ?, ?, ?, ?)",
                            (1, 'user1', 'password1', 'user1@example.com', 1))
        self.conn.commit()
        with patch('userinfo.send_email') as mock_send_email:
            token = userinfo.generate_reset_token('user1', 'user1@example.com')
            mock_send_email.assert_called_with('Password Reset', f'Your password reset token is: {token}', 'user1@example.com')

    def test_reset_password(self):
        # Test resetting a user's password
        self.cursor.execute("INSERT INTO users (id, username, password_hash, email, access_level) VALUES (?, ?, ?, ?, ?)",
                            (1, 'user1', 'password1', 'user1@example.com', 1))
        self.conn.commit()
        token = userinfo.generate_reset_token('user1', 'user1@example.com')
        result = userinfo.reset_password(token, 'new_password1')
        self.assertTrue(result)
        # ... add more assertions as needed

    def test_update_lock_name(self):
        # Test updating the name of a lock
        self.cursor.execute("INSERT INTO locks (id, lock_id, name, location, state, max_users) VALUES (?, ?, ?, ?, ?, ?)",
                            (1, 123, 'Lock 1', 'Location 1', 1, 2))
        self.conn.commit()
        userinfo.update_lock_name(123, 'New Lock Name')
        self.cursor.execute("SELECT name FROM locks WHERE lock_id=123")
        result = self.cursor.fetchone()
        self.assertEqual(result[0], 'New Lock Name')
        # ... add more assertions as needed

    def test_update_lock_location(self):
        # Test updating the location of a lock
        self.cursor.execute("INSERT INTO locks (id, lock_id, name, location, state, max_users) VALUES (?, ?, ?, ?, ?, ?)",
                            (1, 123, 'Lock 1', 'Location 1', 1, 2))
        self.conn.commit()
        userinfo.update_lock_location(123, 'New Location')
        self.cursor.execute("SELECT location FROM locks WHERE lock_id=123")
        result = self.cursor.fetchone()
        self.assertEqual(result[0], 'New Location')
        # ... add more assertions as needed

    def test_update_lock_max_users(self):
        # Test updating the maximum number of users for a lock
        self.cursor.execute("INSERT INTO locks (id, lock_id, name, location, state, max_users) VALUES (?, ?, ?, ?, ?, ?)",
                            (1, 123, 'Lock 1', 'Location 1', 1, 2))
        self.conn.commit()
        userinfo.update_lock_max_users(123, 5)
        self.cursor.execute("SELECT max_users FROM locks WHERE lock_id=123")
        result = self.cursor.fetchone()
        self.assertEqual(result[0], 5)
        # ... add more assertions as needed

    def test_update_lock_id(self):
        # Test updating the ID of a lock
        self.cursor.execute("INSERT INTO locks (id, lock_id, name, location, state, max_users) VALUES (?, ?, ?, ?, ?, ?)",
                            (1, 123, 'Lock 1', 'Location 1', 1, 2))
        self.conn.commit()
        userinfo.update_lock_id(123, 456)
        self.cursor.execute("SELECT lock_id FROM locks WHERE lock_id=456")
        result = self.cursor.fetchone()
        self.assertEqual(result[0], 456)
        # ... add more assertions as needed

    def test_allocate_lock_for_user(self):
        # Test allocating a lock to a user (access level 0)
        self.cursor.execute("INSERT INTO locks (id, lock_id, name, location, state, max_users) VALUES (?, ?, ?, ?, ?, ?)",
                            (1, 123, 'Lock 1', 'Location 1', 1, 2))
        self.cursor.execute("INSERT INTO users (id, username, password_hash, email, access_level) VALUES (?, ?, ?, ?, ?)",
                            (1, 'user1', 'password1', 'user1@example.com', 0))
        self.conn.commit()
        result = userinfo.allocate_lock_for_user(123, 'user1')
        self.assertFalse(result)
        # ... add more assertions as needed

    def test_update_lock_state(self):
        # Test updating the state of a lock
        self.cursor.execute("INSERT INTO locks (id, lock_id, name, location, state, max_users) VALUES (?, ?, ?, ?, ?, ?)",
                            (1, 123, 'Lock 1', 'Location 1', 1, 2))
        self.conn.commit()
        userinfo.update_lock_state(123, 0)
        self.cursor.execute("SELECT state FROM locks WHERE lock_id=123")
        result = self.cursor.fetchone()
        self.assertEqual(result[0], 0)
        # ... add more assertions as needed

if __name__ == '__main__':
    
    unittest.main()