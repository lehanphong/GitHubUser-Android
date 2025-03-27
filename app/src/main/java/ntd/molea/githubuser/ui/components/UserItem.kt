package ntd.molea.githubuser.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ntd.molea.githubuser.data.model.User
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import ntd.molea.githubuser.ui.screens.UserListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserItem(
    user: User,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(128.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
//                .background(color = Color.Red),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.avatarUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "User Avatar",
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)
                    .padding(6.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically),
            )

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxHeight()
            ) {
                Text(
                    text = user.login,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
//                    modifier = Modifier.weight(1f),

                )
//
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                Text(
                    text = user.htmlUrl,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF2196F3), // Material Blue color
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {

    UserItem(user = User(
        "f",
        1,
        "https://avatars.githubusercontent.com/u/101?v=4",
        "https://www.linkedin.com/"
    ), {})
}