package ntd.molea.githubuser.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("login")
    val login: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("avatar_url")
    val avatarUrl: String,
    @SerializedName("html_url")
    val htmlUrl: String,
    @SerializedName("location")
    val location: String? = null,
    @SerializedName("followers")
    val followers: Int? = null,
    @SerializedName("following")
    val following: Int? = null
)