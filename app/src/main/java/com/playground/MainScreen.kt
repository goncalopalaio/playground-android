package com.playground

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.playground.vm.UiState
import com.playground.vm.UsersUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    dashboardState: UiState,
    usersState: UsersUiState,
    onUserIdChange: (String) -> Unit,
    onCreateUser: () -> Unit,
    onRefreshUsers: () -> Unit,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    LaunchedEffect(selectedTab) {
        if (selectedTab == 1) onRefreshUsers()
    }
    Column(modifier = modifier.fillMaxSize()) {
        PrimaryTabRow(selectedTabIndex = selectedTab) {
            listOf("Dashboard", "Users").forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) },
                )
            }
        }
        if (selectedTab == 0) {
            Greeting(dashboardState, Modifier.weight(1f))
        } else {
            UsersScreen(usersState, onUserIdChange, onCreateUser, onRefreshUsers, onUserClick, Modifier.weight(1f))
        }
    }
}

@Composable
private fun UsersScreen(
    state: UsersUiState,
    onUserIdChange: (String) -> Unit,
    onCreateUser: () -> Unit,
    onRefreshUsers: () -> Unit,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().imePadding().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = state.userId,
            onValueChange = onUserIdChange,
            label = { Text("User ID") },
            singleLine = true,
            enabled = !state.isBusy,
            modifier = Modifier.fillMaxWidth(),
        )
        Button(onClick = onCreateUser, enabled = state.userId.isNotBlank() && !state.isBusy) {
            Text("Create user")
        }
        if (state.isBusy) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            TextButton(onClick = onRefreshUsers, enabled = !state.isBusy) { Text("Refresh users") }
        }
        Text("Current users", style = MaterialTheme.typography.titleMedium)
        if (state.users.isEmpty() && !state.isBusy) {
            Text("No users yet. Create one above.")
        }
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.users, key = { it }) { userId ->
                Text(userId, modifier = Modifier.fillMaxWidth().clickable { onUserClick(userId) }.padding(vertical = 8.dp))
            }
        }
    }
}
