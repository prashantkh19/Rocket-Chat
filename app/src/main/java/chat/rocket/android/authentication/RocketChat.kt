package chat.rocket.android.authentication

import chat.rocket.android.authentication.presentation.AuthenticationPresenter
import chat.rocket.android.dagger.DaggerRocketComponent
import javax.inject.Inject

class RocketChat constructor(activity: TemplateActivity,
                             protocol: String,
                             serverDomain: String,
                             name: String,
                             userName: String,
                             userEmail: String,
                             userPassword: String,
                             roomName: String) {

    @Inject
    lateinit var presenter: AuthenticationPresenter

    init {
        DaggerRocketComponent.builder()
                .application(activity.application)
                .activity(activity)
                .build().inject(this)

        presenter.setConnectionParams(
                protocol,
                serverDomain,
                name,
                userName,
                userEmail,
                userPassword,
                roomName)
    }

    fun getState(): String {
        return presenter.auth_state
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

    fun loadChatRoom() {
        presenter.loadChatRoom()
    }

    fun performConnect() {
        presenter.connect()
    }

    private fun connectToServer() {
        presenter.checkServer()
    }

    private fun showChatRoom() = presenter.toChatRoom()

}

const val STATE_LOADING = "state_loading"
const val STATE_READY = "state_ready"
const val STATE_ERROR = "state_error"
