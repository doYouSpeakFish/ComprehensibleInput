package input.comprehensible.data.account

import com.ktin.Singleton

class SessionProvider private constructor() {
    @Volatile
    var token: String? = null

    companion object : Singleton<SessionProvider>() {
        override fun create() = SessionProvider()
    }
}
