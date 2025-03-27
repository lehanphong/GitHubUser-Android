package ntd.molea.githubuser.ui.users

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ntd.molea.githubuser.ui.screens.UserListScreen
import ntd.molea.githubuser.ui.theme.GitHubUserTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersView(
    modifier: Modifier = Modifier,
    viewModel: UsersViewModel = koinViewModel()
) {
    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    GitHubUserTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Github Users") },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.refreshUsers() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }
                )
            },
            modifier = modifier
        ) { paddingValues ->
            UserListScreen(
                modifier = modifier.padding(paddingValues),
                users = users,
                isLoading = isLoading,
                onLoadMore = { viewModel.loadMore() },
                onUserClick = { /* Handle user click */ },
            )
        }
    }
}