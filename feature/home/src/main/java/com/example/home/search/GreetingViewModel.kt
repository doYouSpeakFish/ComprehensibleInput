package com.example.home.search

import androidx.lifecycle.ViewModel
import com.example.data.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class GreetingViewModel @Inject constructor(
    userRepository: UserRepository
) : ViewModel() {
    val uiState = userRepository.userName.map { userName ->
        GreetingUiState(
            usersName = userName
        )
    }
}