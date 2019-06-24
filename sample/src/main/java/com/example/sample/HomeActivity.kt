package com.example.sample

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import chat.rocket.android.authentication.Rocket
import chat.rocket.android.authentication.TemplateActivity
import chat.rocket.android.authentication.presentation.AuthenticationPresenter
import chat.rocket.android.helper.saveCredentials
import chat.rocket.android.util.extensions.showToast
import kotlinx.android.synthetic.main.home_activity.*

class HomeActivity : TemplateActivity() {

    lateinit var rocket: Rocket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        rocket = Rocket(this)
        rocket.loadCredentials()
        button_support.setOnClickListener {
            if (rocket.presenter.auth_state == AuthenticationPresenter.STATE_READY) {
                showMessage("Going to Chat Room")
                rocket.presenter.loadChatRoom()
            } else {
                showMessage("Not Ready")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        currentFragment?.onActivityResult(requestCode, resultCode, data)
    }

    override fun showInvalidServerUrlMessage() {
        showMessage(getString(R.string.msg_invalid_server_url))
    }

    override fun showMessage(resId: Int) {
        showToast(resId)
    }

    override fun showMessage(message: String) {
        showToast(message)
    }

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
        rocket.performConnect()
    }

    override fun saveSmartLockCredentials(usernameOrEmail: String, password: String) {
        saveCredentials(usernameOrEmail, password)
    }
}