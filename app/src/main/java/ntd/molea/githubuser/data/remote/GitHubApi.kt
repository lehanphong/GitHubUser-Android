package ntd.molea.githubuser.data.remote

import ntd.molea.githubuser.data.model.User
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApi {
    @GET("users")
    suspend fun getUsers(
        @Query("per_page") perPage: Int = 20,
        @Query("since") since: Int = 0
    ): List<User>

    @GET("users/{login_username}")
    suspend fun getUserDetails(
        @Path("login_username") loginUsername: String
    ): User
} 