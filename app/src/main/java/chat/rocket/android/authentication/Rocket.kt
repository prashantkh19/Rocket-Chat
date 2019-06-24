package chat.rocket.android.authentication

import chat.rocket.android.authentication.presentation.AuthenticationPresenter
import chat.rocket.android.dagger.DaggerRocketComponent
import chat.rocket.android.util.extensions.sanitize
import javax.inject.Inject

class Rocket constructor(var activity: TemplateActivity) {

    @Inject
    lateinit var presenter: AuthenticationPresenter

    private val protocol = chat.rocket.android.util.protocol

    init {
        DaggerRocketComponent.builder()
                .application(activity.application)
                .activity(activity)
                .build().inject(this)
    }

    fun loadCredentials() {
        presenter.loadCredentials { isAuthenticated ->
            if (isAuthenticated) {
                showChatRoom()
            } else {
                connectToServer()
            }
        }
    }

    fun performConnect() {
        presenter.connect("$protocol${chat.rocket.android.util.serverDomain.sanitize()}")
    }

    fun connectToServer() {
        presenter.checkServer("$protocol${chat.rocket.android.util.serverDomain.sanitize()}")
    }

    fun showChatRoom() = presenter.toChatRoom()

}