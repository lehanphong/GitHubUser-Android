package ntd.molea.githubuser.di

import ntd.molea.githubuser.data.local.GitHubUserDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { GitHubUserDatabase.create(androidContext()) }
    single { get<GitHubUserDatabase>().githubUserDao() }
} 