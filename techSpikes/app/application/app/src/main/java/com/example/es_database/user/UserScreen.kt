package com.example.es_database.user
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.es_database.AddUserDialog

@Composable
fun UserScreen(
    state: UserState,
    onEvent: (UserEvent) -> Unit
) {/*
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(UserEvent.ShowDialog)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add User"
                )
            }
        },
    ) { _ ->
        if(state.isAddingUser) {
            AddUserDialog(state = state, onEvent = onEvent)
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserSortType.values().forEach { sortType ->
                        Row(
                            modifier = Modifier
                                .clickable {
                                    onEvent(UserEvent.SortUsers(sortType))
                                },
                            verticalAlignment = CenterVertically
                        ) {
                            RadioButton(
                                selected = state.userSortType == sortType,
                                onClick = {
                                    onEvent(UserEvent.SortUsers(sortType))
                                }
                            )
                            Text(text = sortType.name)
                        }
                    }
                }
            }
            items(state.users) {user ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${user.username} ${user.email}",
                            fontSize = 20.sp
                        )
                        Text(text = user.password, fontSize = 12.sp)
                    }
                    IconButton(onClick = {
                        onEvent(UserEvent.DeleteUser(user))
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete User"
                        )
                    }
                }
            }
        }
    }

 */
}


enum class UserSortType {

    USERNAME,
    EMAIL
}

