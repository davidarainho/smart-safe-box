package com.example.es_database


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.es_database.ui.RoomGuideAndroidTheme
import com.example.es_database.user.UserDatabase
import com.example.es_database.user.UserScreen
import com.example.es_database.user.UserViewModel


@Suppress("UNCHECKED_CAST")
class MainActivity : ComponentActivity()
{
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            UserDatabase::class.java,
            "users.db"
        ).build()
    }

    private val viewModel by viewModels<UserViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UserViewModel(db.dao) as T
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RoomGuideAndroidTheme {
                val state by viewModel.state.collectAsState()
                UserScreen(state = state, onEvent = viewModel::onEvent)
            }
        }
    }
}
