package ntd.molea.githubuser.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "github_users")
data class UserEntity(
    @PrimaryKey
    val randomKey: String = UUID.randomUUID().toString(),
    val id: Int,
    val login: String,
    val avatarUrl: String,
    val htmlUrl: String,
    val location: String? = null,
    val followers: Int? = null,
    val following: Int? = null
) {
    fun haveDetailInfo() : Boolean {
        return location != null && followers != null && following != null
    }
}