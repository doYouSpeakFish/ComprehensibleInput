package input.comprehensible.backend.email

/** A localised email ready to send: a [subject] line and a plain-text [body]. */
data class EmailContent(val subject: String, val body: String)

/**
 * The localised content for every account email the backend sends, in each [EmailLanguage] the app
 * supports. Verification codes are interpolated into the body where relevant.
 *
 * The product name ("3 Million Words") is a brand and is intentionally left untranslated. Keep the
 * English text in sync with the assertions in the account management API tests.
 */
@Suppress("TooManyFunctions")
object AccountEmails {

    /** Sent when someone tries to register an email that already belongs to a verified account. */
    fun accountAlreadyExists(language: EmailLanguage): EmailContent = when (language) {
        EmailLanguage.ENGLISH -> EmailContent(
            subject = "3 Million Words account already exists",
            body = "A 3 Million Words account already exists for this email address. " +
                "If this was not you, you can safely ignore this message.",
        )
        EmailLanguage.GERMAN -> EmailContent(
            subject = "3 Million Words-Konto besteht bereits",
            body = "Für diese E-Mail-Adresse besteht bereits ein 3 Million Words-Konto. " +
                "Falls Sie das nicht waren, können Sie diese Nachricht ignorieren.",
        )
        EmailLanguage.SPANISH -> EmailContent(
            subject = "La cuenta de 3 Million Words ya existe",
            body = "Ya existe una cuenta de 3 Million Words para esta dirección de correo electrónico. " +
                "Si no fuiste tú, puedes ignorar este mensaje.",
        )
        EmailLanguage.FRENCH -> EmailContent(
            subject = "Le compte 3 Million Words existe déjà",
            body = "Un compte 3 Million Words existe déjà pour cette adresse e-mail. " +
                "Si ce n'était pas vous, vous pouvez ignorer ce message.",
        )
        EmailLanguage.PORTUGUESE -> EmailContent(
            subject = "A conta do 3 Million Words já existe",
            body = "Já existe uma conta do 3 Million Words para este endereço de e-mail. " +
                "Se não foi você, pode ignorar esta mensagem.",
        )
        EmailLanguage.INDONESIAN -> EmailContent(
            subject = "Akun 3 Million Words sudah ada",
            body = "Akun 3 Million Words sudah ada untuk alamat email ini. " +
                "Jika ini bukan Anda, Anda dapat mengabaikan pesan ini.",
        )
    }

    /** Sent with the code that verifies a newly registered email address. */
    fun verifyEmailAddress(language: EmailLanguage, code: String): EmailContent = when (language) {
        EmailLanguage.ENGLISH -> EmailContent(
            subject = "Verify your 3 Million Words email address",
            body = "Use this verification code to verify your 3 Million Words email address: $code",
        )
        EmailLanguage.GERMAN -> EmailContent(
            subject = "Bestätigen Sie Ihre 3 Million Words-E-Mail-Adresse",
            body = "Verwenden Sie diesen Bestätigungscode, um Ihre 3 Million Words-E-Mail-Adresse " +
                "zu bestätigen: $code",
        )
        EmailLanguage.SPANISH -> EmailContent(
            subject = "Verifica tu dirección de correo electrónico de 3 Million Words",
            body = "Usa este código de verificación para verificar tu dirección de correo " +
                "electrónico de 3 Million Words: $code",
        )
        EmailLanguage.FRENCH -> EmailContent(
            subject = "Vérifiez votre adresse e-mail 3 Million Words",
            body = "Utilisez ce code de vérification pour vérifier votre adresse e-mail " +
                "3 Million Words : $code",
        )
        EmailLanguage.PORTUGUESE -> EmailContent(
            subject = "Verifique o seu endereço de e-mail do 3 Million Words",
            body = "Use este código de verificação para verificar o seu endereço de e-mail " +
                "do 3 Million Words: $code",
        )
        EmailLanguage.INDONESIAN -> EmailContent(
            subject = "Verifikasi alamat email 3 Million Words Anda",
            body = "Gunakan kode verifikasi ini untuk memverifikasi alamat email " +
                "3 Million Words Anda: $code",
        )
    }

    /** Sent to the current address with the code that confirms a requested email change. */
    fun confirmEmailChange(language: EmailLanguage, code: String): EmailContent = when (language) {
        EmailLanguage.ENGLISH -> EmailContent(
            subject = "Confirm your 3 Million Words email change",
            body = "Use this verification code to confirm your 3 Million Words email change: $code. " +
                "If this change is unexpected, login to your 3 Million Words account and change " +
                "your password immediately.",
        )
        EmailLanguage.GERMAN -> EmailContent(
            subject = "Bestätigen Sie Ihre 3 Million Words-E-Mail-Änderung",
            body = "Verwenden Sie diesen Bestätigungscode, um Ihre 3 Million Words-E-Mail-Änderung " +
                "zu bestätigen: $code. Falls diese Änderung unerwartet ist, melden Sie sich bei " +
                "Ihrem 3 Million Words-Konto an und ändern Sie sofort Ihr Passwort.",
        )
        EmailLanguage.SPANISH -> EmailContent(
            subject = "Confirma el cambio de correo electrónico de 3 Million Words",
            body = "Usa este código de verificación para confirmar el cambio de tu correo " +
                "electrónico de 3 Million Words: $code. Si este cambio es inesperado, inicia " +
                "sesión en tu cuenta de 3 Million Words y cambia tu contraseña de inmediato.",
        )
        EmailLanguage.FRENCH -> EmailContent(
            subject = "Confirmez le changement d'adresse e-mail 3 Million Words",
            body = "Utilisez ce code de vérification pour confirmer le changement de votre adresse " +
                "e-mail 3 Million Words : $code. Si ce changement est inattendu, connectez-vous à " +
                "votre compte 3 Million Words et changez immédiatement votre mot de passe.",
        )
        EmailLanguage.PORTUGUESE -> EmailContent(
            subject = "Confirme a alteração de e-mail do 3 Million Words",
            body = "Use este código de verificação para confirmar a alteração de e-mail do " +
                "3 Million Words: $code. Se esta alteração for inesperada, entre na sua conta do " +
                "3 Million Words e altere a sua senha imediatamente.",
        )
        EmailLanguage.INDONESIAN -> EmailContent(
            subject = "Konfirmasi perubahan email 3 Million Words Anda",
            body = "Gunakan kode verifikasi ini untuk mengonfirmasi perubahan email " +
                "3 Million Words Anda: $code. Jika perubahan ini tidak terduga, masuk ke akun " +
                "3 Million Words Anda dan segera ubah kata sandi Anda.",
        )
    }

    /** Sent to an address that an account update tried to claim but that is already in use. */
    fun emailUpdateAlreadyInUse(language: EmailLanguage): EmailContent = when (language) {
        EmailLanguage.ENGLISH -> EmailContent(
            subject = "3 Million Words email update attempted",
            body = "An account update attempted to use this email address, but an account already " +
                "has this email.",
        )
        EmailLanguage.GERMAN -> EmailContent(
            subject = "Versuchte 3 Million Words-E-Mail-Änderung",
            body = "Bei einer Kontoaktualisierung wurde versucht, diese E-Mail-Adresse zu verwenden, " +
                "aber ein Konto hat diese E-Mail-Adresse bereits.",
        )
        EmailLanguage.SPANISH -> EmailContent(
            subject = "Intento de cambio de correo electrónico de 3 Million Words",
            body = "Una actualización de cuenta intentó usar esta dirección de correo electrónico, " +
                "pero una cuenta ya tiene este correo electrónico.",
        )
        EmailLanguage.FRENCH -> EmailContent(
            subject = "Tentative de changement d'adresse e-mail 3 Million Words",
            body = "Une mise à jour de compte a tenté d'utiliser cette adresse e-mail, mais un " +
                "compte possède déjà cette adresse e-mail.",
        )
        EmailLanguage.PORTUGUESE -> EmailContent(
            subject = "Tentativa de alteração de e-mail do 3 Million Words",
            body = "Uma atualização de conta tentou usar este endereço de e-mail, mas uma conta já " +
                "tem este e-mail.",
        )
        EmailLanguage.INDONESIAN -> EmailContent(
            subject = "Percobaan perubahan email 3 Million Words",
            body = "Pembaruan akun mencoba menggunakan alamat email ini, tetapi sebuah akun sudah " +
                "memiliki email ini.",
        )
    }

    /** Sent to the new address with the code that verifies an email change. */
    fun verifyEmailChange(language: EmailLanguage, code: String): EmailContent = when (language) {
        EmailLanguage.ENGLISH -> EmailContent(
            subject = "Verify your 3 Million Words email change",
            body = "Use this verification code to verify your 3 Million Words email change: $code",
        )
        EmailLanguage.GERMAN -> EmailContent(
            subject = "Verifizieren Sie Ihre 3 Million Words-E-Mail-Änderung",
            body = "Verwenden Sie diesen Bestätigungscode, um Ihre 3 Million Words-E-Mail-Änderung " +
                "zu verifizieren: $code",
        )
        EmailLanguage.SPANISH -> EmailContent(
            subject = "Verifica el cambio de correo electrónico de 3 Million Words",
            body = "Usa este código de verificación para verificar el cambio de tu correo " +
                "electrónico de 3 Million Words: $code",
        )
        EmailLanguage.FRENCH -> EmailContent(
            subject = "Vérifiez le changement d'adresse e-mail 3 Million Words",
            body = "Utilisez ce code de vérification pour vérifier le changement de votre adresse " +
                "e-mail 3 Million Words : $code",
        )
        EmailLanguage.PORTUGUESE -> EmailContent(
            subject = "Verifique a alteração de e-mail do 3 Million Words",
            body = "Use este código de verificação para verificar a alteração de e-mail do " +
                "3 Million Words: $code",
        )
        EmailLanguage.INDONESIAN -> EmailContent(
            subject = "Verifikasi perubahan email 3 Million Words Anda",
            body = "Gunakan kode verifikasi ini untuk memverifikasi perubahan email " +
                "3 Million Words Anda: $code",
        )
    }

    /** Sent when a password reset is requested for an address with no account. */
    fun passwordResetNoAccount(language: EmailLanguage): EmailContent = when (language) {
        EmailLanguage.ENGLISH -> EmailContent(
            subject = "3 Million Words password reset request",
            body = "A password reset was requested for this email, but you do not have a " +
                "3 Million Words account.",
        )
        EmailLanguage.GERMAN -> EmailContent(
            subject = "3 Million Words-Anfrage zum Zurücksetzen des Passworts",
            body = "Für diese E-Mail-Adresse wurde ein Zurücksetzen des Passworts angefordert, aber " +
                "Sie haben kein 3 Million Words-Konto.",
        )
        EmailLanguage.SPANISH -> EmailContent(
            subject = "Solicitud de restablecimiento de contraseña de 3 Million Words",
            body = "Se solicitó un restablecimiento de contraseña para este correo electrónico, pero " +
                "no tienes una cuenta de 3 Million Words.",
        )
        EmailLanguage.FRENCH -> EmailContent(
            subject = "Demande de réinitialisation du mot de passe 3 Million Words",
            body = "Une réinitialisation du mot de passe a été demandée pour cette adresse e-mail, " +
                "mais vous n'avez pas de compte 3 Million Words.",
        )
        EmailLanguage.PORTUGUESE -> EmailContent(
            subject = "Solicitação de redefinição de senha do 3 Million Words",
            body = "Foi solicitada uma redefinição de senha para este e-mail, mas você não tem uma " +
                "conta do 3 Million Words.",
        )
        EmailLanguage.INDONESIAN -> EmailContent(
            subject = "Permintaan atur ulang kata sandi 3 Million Words",
            body = "Pengaturan ulang kata sandi diminta untuk email ini, tetapi Anda tidak memiliki " +
                "akun 3 Million Words.",
        )
    }

    /** Sent with the code that lets a user reset their password. */
    fun passwordResetCode(language: EmailLanguage, code: String): EmailContent = when (language) {
        EmailLanguage.ENGLISH -> EmailContent(
            subject = "Your 3 Million Words password reset code",
            body = "Use this password reset code to reset your 3 Million Words password: $code",
        )
        EmailLanguage.GERMAN -> EmailContent(
            subject = "Ihr 3 Million Words-Code zum Zurücksetzen des Passworts",
            body = "Verwenden Sie diesen Code zum Zurücksetzen des Passworts, um Ihr " +
                "3 Million Words-Passwort zurückzusetzen: $code",
        )
        EmailLanguage.SPANISH -> EmailContent(
            subject = "Tu código de restablecimiento de contraseña de 3 Million Words",
            body = "Usa este código de restablecimiento de contraseña para restablecer tu contraseña " +
                "de 3 Million Words: $code",
        )
        EmailLanguage.FRENCH -> EmailContent(
            subject = "Votre code de réinitialisation du mot de passe 3 Million Words",
            body = "Utilisez ce code de réinitialisation pour réinitialiser votre mot de passe " +
                "3 Million Words : $code",
        )
        EmailLanguage.PORTUGUESE -> EmailContent(
            subject = "O seu código de redefinição de senha do 3 Million Words",
            body = "Use este código de redefinição para redefinir a sua senha do " +
                "3 Million Words: $code",
        )
        EmailLanguage.INDONESIAN -> EmailContent(
            subject = "Kode atur ulang kata sandi 3 Million Words Anda",
            body = "Gunakan kode atur ulang kata sandi ini untuk mengatur ulang kata sandi " +
                "3 Million Words Anda: $code",
        )
    }
}
