package com.example.home.search

data class GreetingUiState(
    val usersName: String
) {
    companion object {
        val INITIAL = GreetingUiState(
            usersName = ""
        )
    }
}
