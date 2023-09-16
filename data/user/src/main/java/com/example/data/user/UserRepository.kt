package com.example.data.user

import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class UserRepository @Inject constructor() {
    val userName = flowOf("John Doe")
}