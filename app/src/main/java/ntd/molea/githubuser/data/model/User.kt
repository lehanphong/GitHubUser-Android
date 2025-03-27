package ntd.molea.githubuser.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("login")
    val login: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("node_id")
    val avatarUrl: String,
    @SerializedName("gravatar_id")
    val htmlUrl: String,
)