package ntd.molea.githubuser.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ntd.molea.githubuser.data.api.GitHubApi
import ntd.molea.githubuser.data.local.GitHubUserDao
import ntd.molea.githubuser.data.local.GitHubUserEntity
import ntd.molea.githubuser.data.model.User
import ntd.molea.githubuser.utils.Vlog

class UserRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val api: GitHubApi,
    private val dao: GitHubUserDao
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
                    GitHubUserEntity(
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
                GitHubUserEntity(
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

    private fun GitHubUserEntity.toUser(): User {
        return User(
            id = id,
            login = login,
            avatarUrl = avatarUrl,
            htmlUrl = htmlUrl,
        )
    }
} 