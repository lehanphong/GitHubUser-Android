package ntd.molea.githubuser.di

import org.koin.dsl.module

val appModule = module {
    includes(networkModule, viewModelModule, databaseModule)
}