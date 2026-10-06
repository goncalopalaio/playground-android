package com.playground

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .then(Modifier.wrapContentSize(Alignment.Center))
    ) {
        when (state) {
            UiState.Loading -> {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            is UiState.Dashboard -> {
                LazyColumn {
                    item {
                        Text(
                            text = "Hello ${state.name}! \n ${state.stats}",
                            modifier = Modifier,
                            textAlign = TextAlign.Center
                        )
                    }

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PlaygroundTheme {
        Greeting(UiState.Dashboard("Android", "Stats"))
    }
}