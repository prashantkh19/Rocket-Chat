package chat.rocket.android.authentication

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import chat.rocket.android.authentication.presentation.AuthenticationPresenter
import chat.rocket.android.authentication.presentation.RocketChatView
import chat.rocket.android.dagger.DaggerRocketComponent

class RocketChat<T> constructor(activity: T,
                                protocol: String,
                                val serverDomain: String,
                                name: String,
                                userName: String,
                                userEmail: String,
                                userPassword: String,
                                roomName: String) where T : AppCompatActivity, T : RocketChatView {

    internal var presenter: AuthenticationPresenter

    init {
        val wrapper = RocketChatWrapper()
        DaggerRocketComponent.builder()
                .application(activity.application)
                .activity(activity)
                .build()
                .inject(wrapper)

        presenter = AuthenticationPresenter(activity,
                wrapper.strategy,
                wrapper.navigator,
                wrapper.getCurrentServerInteractor,
                wrapper.getAccountInteractor,
                wrapper.settingsRepository,
                wrapper.localRepository,
                wrapper.tokenRepository,
                wrapper.saveServerInteractor,
                wrapper.refreshSettingsInteractor,
                wrapper.getAccountsInteractor,
                wrapper.settingsInteractor,
                wrapper.saveCurrentServer,
                wrapper.factory,
                wrapper.saveAccountInteractor,
                wrapper.analyticsManager,
                wrapper.dbManagerFactory,
                wrapper.userHelper
        )

        presenter.setConnectionParams(
                protocol,
                serverDomain,
                name,
                userName,
                userEmail,
                userPassword,
                roomName)
    }

    fun getState(): MutableLiveData<String> {
        return presenter.setupState
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

    fun logoutCurrentUser() {
        presenter.logout()
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
const val INVALID_SERVER_URL = "invalid_server_url"


