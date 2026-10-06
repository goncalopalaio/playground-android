package com.playground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.playground.ui.theme.PlaygroundTheme
import com.playground.vm.UserStoreViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class UserStoreActivity : ComponentActivity() {
    private val storeViewModel: UserStoreViewModel by viewModel {
        parametersOf(requireNotNull(intent.getStringExtra(USER_ID)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getStringExtra(USER_ID).isNullOrBlank()) {
            finish()
            return
        }
        enableEdgeToEdge()
        setContent {
            val state by storeViewModel.state.collectAsStateWithLifecycle()
            PlaygroundTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    Column(
                        modifier = Modifier.fillMaxSize().padding(padding).imePadding().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        TextButton(onClick = { finish() }) { Text("Back to users") }
                        Text("Store for ${storeViewModel.userId}", style = MaterialTheme.typography.headlineSmall)
                        OutlinedTextField(
                            value = state.key,
                            onValueChange = storeViewModel::updateKey,
                            label = { Text("Key") },
                            singleLine = true,
                            enabled = !state.isBusy,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = state.value,
                            onValueChange = storeViewModel::updateValue,
                            label = { Text("Value") },
                            enabled = !state.isBusy,
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Button(onClick = storeViewModel::save, enabled = state.key.isNotBlank() && !state.isBusy) {
                            Text("Add or update")
                        }
                        if (state.isBusy) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        state.error?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                            TextButton(onClick = storeViewModel::refresh, enabled = !state.isBusy) { Text("Refresh values") }
                        }
                        Text("Current values · Most recent first", style = MaterialTheme.typography.titleMedium)
                        if (state.entries.isEmpty() && !state.isBusy) Text("No values yet.")
                        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(state.entries, key = { it.key }) { entry ->
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                        .clickable(enabled = !state.isBusy) { storeViewModel.editEntry(entry) }
                                        .padding(vertical = 8.dp),
                                ) {
                                    Text(entry.key, style = MaterialTheme.typography.titleSmall)
                                    Text(entry.value)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    companion object { const val USER_ID = "user_id" }
}
