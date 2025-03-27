package ntd.molea.githubuser.di

import ntd.molea.githubuser.ui.users.UsersViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        UsersViewModel(get())
    }
}