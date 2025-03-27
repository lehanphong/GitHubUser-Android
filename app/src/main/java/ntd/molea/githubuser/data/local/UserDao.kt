package ntd.molea.githubuser.data.local

import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM github_users ORDER BY id ASC LIMIT :limit OFFSET :offset")
    fun getUsersWithPagination(limit: Int, offset: Int): List<UserEntity>

    @Query("SELECT COUNT(*) FROM github_users")
    suspend fun getTotalUsersCount(): Int

    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM github_users")
    suspend fun deleteAllUsers()

    @Query("SELECT * FROM github_users WHERE login = :loginUsername LIMIT 1")
    suspend fun getUserByLogin(loginUsername: String): UserEntity?

    @Update
    suspend fun updateUser(user: UserEntity)
} 