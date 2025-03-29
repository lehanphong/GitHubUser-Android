package ntd.molea.githubuser.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ntd.molea.githubuser.data.remote.ApiException
import ntd.molea.githubuser.data.model.User
import ntd.molea.githubuser.data.repository.UserRepository
import ntd.molea.githubuser.utils.DLog

class UsersViewModel(
    private val repository: UserRepository
) : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentPage = 0
    private val perPage = 20

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow() //todo: with docs rule!

    init {
        fetchUsers()
    }

    private fun handleError(e: Exception) {
        val errorMessage = when (e) {
            is ApiException.ClientError -> "ClientError: ${e.message}"
            is ApiException.ServerError -> "ServerError: ${e.message}"
            is ApiException.NetworkError -> "NetworkError: ${e.message}"
            is ApiException.UnknownError -> "UnknownError: ${e.message}"
            else -> "Exception: ${e.message}"
        }
        _error.value = errorMessage
        DLog.e(e)
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val result = repository.getUsers(currentPage * perPage, perPage)
                _users.value += result
                currentPage++
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshUsers() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                currentPage = 0
                _users.value = arrayListOf()
                val result = repository.refreshUsers(currentPage * perPage, perPage)
                _users.value += result
                currentPage++
            } catch (e: Exception) {
                handleError(e)
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