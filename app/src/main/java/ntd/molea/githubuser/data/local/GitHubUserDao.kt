package ntd.molea.githubuser.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GitHubUserDao {
    @Query("SELECT * FROM github_users ORDER BY id ASC LIMIT :limit OFFSET :offset")
    fun getUsersWithPagination(limit: Int, offset: Int): List<GitHubUserEntity>

    @Query("SELECT COUNT(*) FROM github_users")
    suspend fun getTotalUsersCount(): Int

    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun insertUsers(users: List<GitHubUserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: GitHubUserEntity)

    @Query("DELETE FROM github_users")
    suspend fun deleteAllUsers()
} 