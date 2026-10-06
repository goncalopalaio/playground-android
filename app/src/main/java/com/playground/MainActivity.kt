package com.playground

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.playground.data.remote.Photo
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.playground.ui.theme.PlaygroundTheme
import com.playground.vm.MainViewModel
import com.playground.vm.UiState
import com.playground.vm.UsersViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModel()
    private val usersViewModel: UsersViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val stats by viewModel.state.collectAsStateWithLifecycle()
            val usersState by usersViewModel.state.collectAsStateWithLifecycle()
            PlaygroundTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        dashboardState = stats,
                        usersState = usersState,
                        onUserIdChange = usersViewModel::updateUserId,
                        onCreateUser = usersViewModel::createUser,
                        onRefreshUsers = usersViewModel::refresh,
                        onUserClick = { userId ->
                            startActivity(Intent(this, UserStoreActivity::class.java).putExtra(UserStoreActivity.USER_ID, userId))
                        },
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(state: UiState, modifier: Modifier = Modifier) {
    when (state) {
        UiState.Loading -> Box(
            modifier = modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        is UiState.Error -> Text(
            text = state.message,
            color = MaterialTheme.colorScheme.error,
            modifier = modifier.padding(16.dp),
        )
        is UiState.Dashboard -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item { Text("Hello ${state.name}!", style = MaterialTheme.typography.headlineSmall) }
            items(state.photos, key = { it.id }) { photo ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(photo.title, style = MaterialTheme.typography.titleMedium)
                    SubcomposeAsyncImage(
                        model = photo.url,
                        contentDescription = photo.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            }
                        },
                        error = { Text("Unable to load image", modifier = Modifier.padding(16.dp)) },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PlaygroundTheme {
        Greeting(UiState.Dashboard("Android", listOf(
            Photo(1, 1, "A sample photo", "https://picsum.photos/seed/1/600", "https://picsum.photos/seed/1/150"),
        )))
    }
}
