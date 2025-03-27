package ntd.molea.githubuser.ui.users

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GithubUserList(users: List<GithubUser>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Github Users") },
                navigationIcon = {
                    IconButton(onClick = { /*TODO: Implement back navigation*/ }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
//                backgroundColor = MaterialTheme.colors.primary // Add background color for better visual consistency
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.padding(8.dp)
        ) {
            items(users) { user ->
                GithubUserItem(user = user)
            }
        }
    }
}

@Composable
fun GithubUserItem(user: GithubUser) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.avatar_url)
                    .crossfade(true)
                    .build(),
                contentDescription = "Avatar of ${user.login}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray) // Placeholder background
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = user.login,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = user.html_url,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Blue,
                    modifier = Modifier.clickable {
                        // TODO: Implement open URL
                    }
                )
            }
        }
    }
}

data class GithubUser(
    val login: String,
    val avatar_url: String,
    val html_url: String
)

//For preview
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val sampleUsers = listOf(
        GithubUser("David", "https://example.com/avatar1.png", "https://www.linkedin.com/"),
        GithubUser("Lisa", "https://example.com/avatar2.png", "https://www.linkedin.com/"),
        GithubUser("David Patel", "https://example.com/avatar3.png", "https://www.linkedin.com/"),
        GithubUser("Danie", "https://example.com/avatar4.png", "https://www.linkedin.com/"),
        GithubUser("Mary Black", "https://example.com/avatar5.png", "https://www.linkedin.com/")
    )
    GithubUserList(users = sampleUsers)
}