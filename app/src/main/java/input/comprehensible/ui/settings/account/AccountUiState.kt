package input.comprehensible.ui.settings.account

data class AccountUiState(
    val placeholder: Unit = Unit,
) {
    companion object {
        val INITIAL = AccountUiState()
    }
}
