package com.example.es_database.user
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.lifecycle.viewModelScope
import com.example.es_database.SortType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserViewModel (
    private val dao: UserDao
) : ViewModel() {

    private val _sortType= MutableStateFlow(SortType.USERNAME)
    private val _users =_sortType
        .flatMapLatest { sortType ->
            when(sortType) {
                SortType.USERNAME -> dao.getUserByUsername()
                SortType.EMAIL -> dao.getUserByEmail()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(UserState())

    val state = combine(_state, _sortType, _users) { state, sortType, users ->
        state.copy(
            users = users,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserState())

    fun onEvent(event: UserEvent)
    {
        when(event)
        {
            is UserEvent.DeleteUser ->
            {
                viewModelScope.launch {
                    dao.deleteUser(event.user)
                }
            }

            UserEvent.HideDialog -> {
                _state.update { it.copy(
                    isAddingUser = false
                ) }
            }


            UserEvent.SaveUser -> {
                val username = state.value.username
                val email = state.value.email
                val password = state.value.password

                if(username.isBlank() || email.isBlank() || password.isBlank()) {
                    return
                }

                val user = User(
                    username = username,
                    email = email,
                    password = password
                )
                viewModelScope.launch {
                    dao.upsertUser(user)
                }
                _state.update { it.copy(
                    isAddingUser = false,
                    username = "",
                    email = "",
                    password = ""
                ) }
            }

            is UserEvent.SetEmail -> {
                _state.update { it.copy(
                    email = event.email
                ) }
            }

            is UserEvent.SetPassword ->  {
                _state.update { it.copy(
                    password = event.password
                ) }
            }

            is UserEvent.SetUsername ->  {
                _state.update { it.copy(
                    username = event.username
                ) }
            }

            UserEvent.ShowDialog ->  {
                _state.update { it.copy(
                    isAddingUser = true
                ) }
            }
            is UserEvent.SortUsers -> {
                _sortType.value=event.sortType
            }
        }



    }




















}