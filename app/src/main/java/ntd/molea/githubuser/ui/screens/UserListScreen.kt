package ntd.molea.githubuser.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ntd.molea.githubuser.data.model.User
import ntd.molea.githubuser.ui.components.UserItem
import ntd.molea.githubuser.ui.theme.GitHubUserTheme
import ntd.molea.githubuser.ui.users.GithubUser
import ntd.molea.githubuser.ui.users.GithubUserList
import ntd.molea.githubuser.utils.Vlog

@Composable
fun UserListScreen(
    users: List<User>,
    isLoading: Boolean = false,
    onLoadMore: () -> Unit = {},
    onUserClick: (User) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0)
            // Load more when we're within 5 items of the bottom
            lastVisibleItemIndex > (totalItemsNumber - 5) && totalItemsNumber > 0
        }
    }

    LaunchedEffect(shouldLoadMore, isLoading) {
        Vlog.d("UsersView", "loadMore shouldLoadMore:$shouldLoadMore isLoading:$isLoading")
        if (shouldLoadMore && !isLoading) {
            onLoadMore()
        }
    }

    Box(
        modifier = modifier
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(users) { user ->
                UserItem(
                    user = user,
                    onClick = { onUserClick(user) }
                )
            }

            item {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }


    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val sampleUsers = listOf(
        User(
            "a",
            1,
            "https://avatars.githubusercontent.com/u/101?v=4",
            "https://www.linkedin.com/"
        ),
        User(
            "g",
            1,
            "https://avatars.githubusercontent.com/u/101?v=4",
            "https://www.linkedin.com/"
        ),
        User(
            "h",
            1,
            "https://avatars.githubusercontent.com/u/101?v=4",
            "https://www.linkedin.com/"
        ),
        User(
            "s",
            1,
            "https://avatars.githubusercontent.com/u/101?v=4",
            "https://www.linkedin.com/"
        ),
        User(
            "f",
            1,
            "https://avatars.githubusercontent.com/u/101?v=4",
            "https://www.linkedin.com/"
        ),
    )
    UserListScreen(users = sampleUsers)
}