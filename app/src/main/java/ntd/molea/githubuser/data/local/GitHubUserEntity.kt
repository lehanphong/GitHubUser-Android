package ntd.molea.githubuser.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "github_users")
data class GitHubUserEntity(
    @PrimaryKey
    val randomKey: String = UUID.randomUUID().toString(),
    val id: Int,
    val login: String,
    val avatarUrl: String,
    val htmlUrl: String,
)