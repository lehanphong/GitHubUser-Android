package ntd.molea.githubuser.di

import kotlinx.coroutines.Dispatchers
import ntd.molea.githubuser.data.api.GitHubApi
import ntd.molea.githubuser.data.api.GitHubInterceptor
import ntd.molea.githubuser.data.local.UserDao
import ntd.molea.githubuser.data.repository.UserRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

sealed class RetrofitInstance {
    object GitHub : RetrofitInstance()
    object OtherDomain : RetrofitInstance()
}

val networkModule = module {
    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single {
        GitHubInterceptor()
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .addInterceptor(get<GitHubInterceptor>())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single(named(RetrofitInstance.GitHub.javaClass.name)) {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //for coming soon :D
    single(named(RetrofitInstance.OtherDomain.javaClass.name)) {
        Retrofit.Builder()
            .baseUrl("https://api.otherdomain.com/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        get<Retrofit>(qualifier = named(RetrofitInstance.GitHub.javaClass.name)).create(GitHubApi::class.java)
    }

    single {
        UserRepository(Dispatchers.IO, get<GitHubApi>(), get<UserDao>())
    }
} 