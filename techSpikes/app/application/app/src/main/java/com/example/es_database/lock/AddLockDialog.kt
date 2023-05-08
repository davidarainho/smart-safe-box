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
import com.example.es_database.lock.LockEvent
import com.example.es_database.lock.LockState

@Composable
fun AddLockDialog(
    state: LockState,
    onEvent: (LockEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onEvent(LockEvent.HideDialog)
        },
        title = { Text(text = "Add Lock") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = state.lock_name,
                    onValueChange = {
                        onEvent(LockEvent.SetLockName(it))
                    },
                    placeholder = {
                        Text(text = "Lock Name")
                    }
                )
                TextField(
                    value = state.user_last_access,
                    onValueChange = {
                        onEvent(LockEvent.SetUserLastAccess(it))
                    },
                    placeholder = {
                        Text(text = "User that Last Accessed")
                    }
                )
                TextField(
                    value = state.number_of_users?.toString() ?: "",
                    onValueChange = {
                        onEvent(LockEvent.SetNumberOfUsers(it.toIntOrNull()?: 0))
                    },
                    placeholder = {
                        Text(text = "Number of Users")
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
                    onEvent(LockEvent.SaveLock)
                }) {
                    Text(text = "Add Lock")
                }
            }
        }
    )
}
