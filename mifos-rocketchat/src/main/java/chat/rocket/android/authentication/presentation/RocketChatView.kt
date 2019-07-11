package chat.rocket.android.authentication.presentation

import chat.rocket.android.authentication.server.presentation.VersionCheckView

interface RocketChatView : VersionCheckView {
    fun saveSmartLockCredentials(usernameOrEmail: String, password: String)
}