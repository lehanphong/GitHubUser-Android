package ntd.molea.githubuser.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ntd.molea.githubuser.data.remote.GitHubApi
import ntd.molea.githubuser.data.local.UserDao
import ntd.molea.githubuser.data.local.UserEntity
import ntd.molea.githubuser.data.model.User
import ntd.molea.githubuser.utils.DLog
import java.util.UUID
import ntd.molea.githubuser.data.remote.ApiException

interface UserRepository {
    suspend fun getUsers(since: Int = 0, perPage: Int = 20): List<User>
    suspend fun refreshUsers(since: Int = 0, perPage: Int = 20): List<User>
    suspend fun getUserDetails(loginUsername: String): User
}

class UserRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher,
    private val api: GitHubApi,
    private val dao: UserDao
) : UserRepository {
    override suspend fun getUsers(since: Int, perPage: Int): List<User> {
        return withContext(ioDispatcher) {
            try {
                // Get data from cache
                val cachedUsers = dao.getUsersWithPagination(perPage, since)

                // try to fetch more from API
                if (cachedUsers.isEmpty()) {
                    val apiUsers = try {
                        api.getUsers(perPage, since)
                    } catch (e: retrofit2.HttpException) {
                        when (e.code()) {
                            in 400..499 -> throw ApiException.ClientError(e.code())
                            in 500..599 -> throw ApiException.ServerError(e.code())
                            else -> throw ApiException.UnknownError()
                        }
                    } catch (e: java.io.IOException) {
                        throw ApiException.NetworkError()
                    }

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
            } catch (e: Exception) {
                DLog.e(e)
                throw e
            }
        }
    }

    override suspend fun refreshUsers(since: Int, perPage: Int): List<User> {
        return withContext(ioDispatcher) {
            try {
                dao.deleteAllUsers() //clear cache
                val users = try {
                    api.getUsers(perPage, since)
                } catch (e: retrofit2.HttpException) {
                    when (e.code()) {
                        in 400..499 -> throw ApiException.ClientError(e.code())
                        in 500..599 -> throw ApiException.ServerError(e.code())
                        else -> throw ApiException.UnknownError()
                    }
                } catch (e: java.io.IOException) {
                    throw ApiException.NetworkError()
                }

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
            } catch (e: Exception) {
                DLog.e(e)
                throw e
            }
        }
    }

    override suspend fun getUserDetails(loginUsername: String): User {
        return withContext(ioDispatcher) {
            try {
                // Check if user is in cache
                val cachedUser = dao.getUserByLogin(loginUsername)
                if (cachedUser != null && cachedUser.haveDetailInfo()) {
                    return@withContext cachedUser.toUser()
                }

                // Fetch from API if not in cache
                val apiUser = try {
                    api.getUserDetails(loginUsername)
                } catch (e: retrofit2.HttpException) {
                    when (e.code()) {
                        in 400..499 -> throw ApiException.ClientError(e.code())
                        in 500..599 -> throw ApiException.ServerError(e.code())
                        else -> throw ApiException.UnknownError()
                    }
                } catch (e: java.io.IOException) {
                    throw ApiException.NetworkError()
                }
                cachedUser?.let {
                    val entity = UserEntity(
                        randomKey = it.randomKey,
                        id = apiUser.id,
                        login = apiUser.login,
                        avatarUrl = apiUser.avatarUrl,
                        htmlUrl = apiUser.htmlUrl,
                        location = apiUser.location,
                        followers = apiUser.followers,
                        following = apiUser.following
                    )
                    dao.updateUser(entity)
                }
                return@withContext apiUser
            } catch (e: Exception) {
                DLog.e(e)
                throw e
            }
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