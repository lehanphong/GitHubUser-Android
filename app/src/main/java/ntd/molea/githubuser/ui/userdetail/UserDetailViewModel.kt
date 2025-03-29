package ntd.molea.githubuser.ui.userdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ntd.molea.githubuser.data.model.User
import ntd.molea.githubuser.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ntd.molea.githubuser.data.remote.ApiException
import ntd.molea.githubuser.utils.DLog

class UserDetailViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()  //todo: with docs rule!

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

    fun getUserDetails(loginUsername: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val userDetails = userRepository.getUserDetails(loginUsername)
                _user.value = userDetails
            } catch (e: Exception) {
                _user.value = null
                handleError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}