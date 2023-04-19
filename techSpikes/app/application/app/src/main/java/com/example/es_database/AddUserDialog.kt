package com.example.es_database

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.es_database.user.UserEvent
import com.example.es_database.user.UserState

@Composable
fun AddUserDialog(
    state: UserState,
    onEvent: (UserEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onEvent(UserEvent.HideDialog)
        },
        title = { Text(text = "Add contact") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = state.username,
                    onValueChange = {
                        onEvent(UserEvent.SetUsername(it))
                    },
                    placeholder = {
                        Text(text = "Username")
                    }
                )
                TextField(
                    value = state.email,
                    onValueChange = {
                        onEvent(UserEvent.SetEmail(it))
                    },
                    placeholder = {
                        Text(text = "E-mail")
                    }
                )
                TextField(
                    value = state.password,
                    onValueChange = {
                        onEvent(UserEvent.SetPassword(it))
                    },
                    placeholder = {
                        Text(text = "Password")
                    }
                )
            }
        },
        buttons = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(onClick = {
                    onEvent(UserEvent.SaveUser)
                }) {
                    Text(text = "Save")
                }
            }
        }
    )
}