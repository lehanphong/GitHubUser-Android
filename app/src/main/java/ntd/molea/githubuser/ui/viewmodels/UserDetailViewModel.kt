package ntd.molea.githubuser.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ntd.molea.githubuser.data.model.User
import ntd.molea.githubuser.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ntd.molea.githubuser.utils.Vlog

class UserDetailViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    fun getUserDetails(loginUsername: String) {
        viewModelScope.launch {
            try {
                val userDetails = userRepository.getUserDetails(loginUsername)
                _user.value = userDetails
            } catch (e: Exception) {
                _user.value = null
            }
        }
    }
} 