package input.comprehensible.backend.email

interface EmailDataSource {
    suspend fun sendEmail(to: String, subject: String, textBody: String)
}

