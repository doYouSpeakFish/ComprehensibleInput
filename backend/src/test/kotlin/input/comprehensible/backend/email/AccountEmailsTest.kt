package input.comprehensible.backend.email

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AccountEmailsTest {

    @Test
    fun `every email has a non-blank subject and body in every language`() {
        forEachEmail { language, content ->
            assertTrue(content.subject.isNotBlank()) { "Blank subject for $language" }
            assertTrue(content.body.isNotBlank()) { "Blank body for $language" }
        }
    }

    @Test
    fun `every email names the product in every language`() {
        forEachEmail { language, content ->
            assertTrue(content.subject.contains(PRODUCT_NAME) || content.body.contains(PRODUCT_NAME)) {
                "Email '${content.subject}' for $language does not mention $PRODUCT_NAME"
            }
        }
    }

    @Test
    fun `code-bearing emails include the verification code in every language`() {
        EmailLanguage.entries.forEach { language ->
            codeBearingEmails(language).forEach { content ->
                assertTrue(content.body.contains(CODE)) {
                    "Email '${content.subject}' for $language is missing the code"
                }
            }
        }
    }

    private fun forEachEmail(action: (EmailLanguage, EmailContent) -> Unit) {
        EmailLanguage.entries.forEach { language ->
            allEmails(language).forEach { content -> action(language, content) }
        }
    }

    private fun allEmails(language: EmailLanguage): List<EmailContent> = listOf(
        AccountEmails.accountAlreadyExists(language),
        AccountEmails.emailUpdateAlreadyInUse(language),
        AccountEmails.passwordResetNoAccount(language),
    ) + codeBearingEmails(language)

    private fun codeBearingEmails(language: EmailLanguage): List<EmailContent> = listOf(
        AccountEmails.verifyEmailAddress(language, CODE),
        AccountEmails.confirmEmailChange(language, CODE),
        AccountEmails.verifyEmailChange(language, CODE),
        AccountEmails.passwordResetCode(language, CODE),
    )

    private companion object {
        const val CODE = "135790"
        const val PRODUCT_NAME = "3 Million Words"
    }
}
