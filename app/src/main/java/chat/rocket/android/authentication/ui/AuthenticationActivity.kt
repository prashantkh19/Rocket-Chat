package chat.rocket.android.authentication.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import chat.rocket.android.R
import chat.rocket.android.analytics.event.ScreenViewEvent
import chat.rocket.android.authentication.domain.model.getLoginDeepLinkInfo
import chat.rocket.android.authentication.presentation.AuthenticationPresenter
import chat.rocket.android.authentication.presentation.AuthenticationView
import chat.rocket.android.helper.saveCredentials
import chat.rocket.android.util.extensions.*
import chat.rocket.common.util.ifNull
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_authentication_server.*
import javax.inject.Inject

class AuthenticationActivity : AppCompatActivity(), AuthenticationView {

    private val protocol = chat.rocket.android.util.protocol

    @Inject
    lateinit var presenter: AuthenticationPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        loadCredentials()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        currentFragment?.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadCredentials() {
        presenter.loadCredentials { isAuthenticated ->
            if (isAuthenticated) {
                showChatRoom()
            } else {
                connectToServer()
            }
        }
    }

    private fun performConnect() {
        presenter.connect("$protocol${chat.rocket.android.util.serverDomain.sanitize()}")
    }

    private fun connectToServer() {
        presenter.checkServer("$protocol${chat.rocket.android.util.serverDomain.sanitize()}")
    }

    private fun showChatRoom() = presenter.toChatRoom()

    override fun showInvalidServerUrlMessage() {
        showMessage(getString(R.string.msg_invalid_server_url))
    }

    override fun showMessage(resId: Int) {
        showToast(resId)
    }

    override fun showMessage(message: String) {
        showToast(message)}

    override fun showGenericErrorMessage() {
        showMessage(getString(R.string.msg_generic_error))
    }

    override fun showLoading() {
        view_loading.isVisible = true
    }

    override fun hideLoading() {
        view_loading.isVisible = false
    }

    override fun alertNotRecommendedVersion() {
        showMessage("Alert Not Recommended Version")
    }

    override fun blockAndAlertNotRequiredVersion() {
        showMessage("Block and Alert Not Required Version")
    }

    override fun versionOk() {
        performConnect()
    }

    override fun saveSmartLockCredentials(usernameOrEmail: String, password: String) {
        saveCredentials(usernameOrEmail, password)
    }

}