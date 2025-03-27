package ntd.molea.githubuser.data.repository

import ntd.molea.githubuser.data.api.GitHubApi
import ntd.molea.githubuser.data.model.User

class UserRepository(private val api: GitHubApi) {
    suspend fun getUsers(perPage: Int = 20, since: Int = 0): List<User> {
        return api.getUsers(perPage, since)
    }
} 