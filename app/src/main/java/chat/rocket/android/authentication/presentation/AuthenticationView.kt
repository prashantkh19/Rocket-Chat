package chat.rocket.android.authentication.presentation

import chat.rocket.android.authentication.server.presentation.ServerView
import chat.rocket.android.authentication.server.presentation.VersionCheckView
import chat.rocket.android.core.behaviours.LoadingView
import chat.rocket.android.core.behaviours.MessageView

interface AuthenticationView : ServerView {
    fun saveSmartLockCredentials(usernameOrEmail: String, password: String)
}