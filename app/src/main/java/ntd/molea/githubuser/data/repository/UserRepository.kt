package ntd.molea.githubuser.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ntd.molea.githubuser.data.api.GitHubApi
import ntd.molea.githubuser.data.local.UserDao
import ntd.molea.githubuser.data.local.UserEntity
import ntd.molea.githubuser.data.model.User
import ntd.molea.githubuser.utils.Vlog
import java.util.UUID

class UserRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val api: GitHubApi,
    private val dao: UserDao
) {
    suspend fun getUsers(since: Int = 0, perPage: Int = 20): List<User> {
        return withContext(ioDispatcher) {
            // Get data from cache
            val cachedUsers = dao.getUsersWithPagination(perPage, since)
            Vlog.d("DUCCHECK", "cachedUsers size: ${cachedUsers.size} since:$since")

            // try to fetch more from API
            if (cachedUsers.isEmpty()) {
                val apiUsers = api.getUsers(perPage, since)
                val entities = apiUsers.map { user ->
                    UserEntity(
                        id = user.id,
                        login = user.login,
                        avatarUrl = user.avatarUrl,
                        htmlUrl = user.htmlUrl,
                    )
                }
                dao.insertUsers(entities)
                return@withContext entities.map { it.toUser() }
            }

            // If we have data in cache, return it
            return@withContext cachedUsers.map { it.toUser() }
        }
    }

    suspend fun refreshUsers(since: Int = 0, perPage: Int = 20): List<User> {
        return withContext(ioDispatcher) {
            dao.deleteAllUsers() //clear cache
            val users = api.getUsers(perPage, since)
            val entities = users.map { user ->
                UserEntity(
                    id = user.id,
                    login = user.login,
                    avatarUrl = user.avatarUrl,
                    htmlUrl = user.htmlUrl,
                )
            }
            dao.insertUsers(entities)
            return@withContext users
        }
    }

    suspend fun getUserDetails(loginUsername: String): User {
        return withContext(ioDispatcher) {
            // Check if user is in cache
            val cachedUser = dao.getUserByLogin(loginUsername)
            if (cachedUser != null && cachedUser.haveDetailInfo()) {
                return@withContext cachedUser.toUser()
            }

            // Fetch from API if not in cache
            val apiUser = api.getUserDetails(loginUsername)
            val entity = UserEntity(
                randomKey = cachedUser?.randomKey ?: UUID.randomUUID().toString(),
                id = apiUser.id,
                login = apiUser.login,
                avatarUrl = apiUser.avatarUrl,
                htmlUrl = apiUser.htmlUrl,
                location = apiUser.location,
                followers = apiUser.followers,
                following = apiUser.following
            )
            dao.updateUser(entity)
            return@withContext apiUser
        }
    }

    private fun UserEntity.toUser(): User {
        return User(
            id = id,
            login = login,
            avatarUrl = avatarUrl,
            htmlUrl = htmlUrl,
            location = location,
            followers = followers,
            following = following
        )
    }
} 