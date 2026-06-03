package input.comprehensible.features.account

import input.comprehensible.ComprehensibleInputTestScope

fun ComprehensibleInputTestScope.onAccount(block: AccountRobot.() -> Unit) {
    AccountRobot(composeRule).block()
}

fun ComprehensibleInputTestScope.onSignUp(block: SignUpRobot.() -> Unit) {
    SignUpRobot(composeRule).block()
}

fun ComprehensibleInputTestScope.onVerifyEmail(block: VerifyEmailRobot.() -> Unit) {
    VerifyEmailRobot(composeRule).block()
}
