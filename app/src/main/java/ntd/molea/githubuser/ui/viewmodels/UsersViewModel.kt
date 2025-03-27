package ntd.molea.githubuser.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ntd.molea.githubuser.data.model.User
import ntd.molea.githubuser.data.repository.UserRepository
import ntd.molea.githubuser.utils.Vlog

class UsersViewModel(
    private val repository: UserRepository
) : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentPage = 0
    private val perPage = 20

    init {
        Vlog.d("DUCCHECK", "UsersViewModel init")
        fetchUsers()
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Vlog.d("DUCCHECK", "currentPage:$currentPage")
                val result = repository.getUsers(currentPage * perPage, perPage)
                _users.value += result
                currentPage++
            } catch (e: Exception) {
                Vlog.e(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshUsers() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                currentPage = 0
                _users.value = arrayListOf()
                val result = repository.refreshUsers(currentPage * perPage, perPage)
                _users.value += result
                currentPage++
            } catch (e: Exception) {
                Vlog.e(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMore() {
        if (!_isLoading.value) {
            fetchUsers()
        }
    }
}