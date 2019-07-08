package chat.rocket.android.authentication.presentation

import chat.rocket.android.authentication.server.presentation.ServerView

interface RocketChatView : ServerView {
    fun saveSmartLockCredentials(usernameOrEmail: String, password: String)
}