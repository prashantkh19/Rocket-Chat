package chat.rocket.android.authentication

import chat.rocket.android.analytics.AnalyticsManager
import chat.rocket.android.authentication.presentation.AuthenticationNavigator
import chat.rocket.android.core.lifecycle.CancelStrategy
import chat.rocket.android.db.DatabaseManagerFactory
import chat.rocket.android.helper.UserHelper
import chat.rocket.android.infrastructure.LocalRepository
import chat.rocket.android.server.domain.*
import chat.rocket.android.server.infrastructure.RocketChatClientFactory
import javax.inject.Inject

class RocketChatWrapper {
    @Inject
    internal lateinit var strategy: CancelStrategy
    @Inject
    internal lateinit var navigator: AuthenticationNavigator
    @Inject
    internal lateinit var getCurrentServerInteractor: GetCurrentServerInteractor
    @Inject
    internal lateinit var getAccountInteractor: GetAccountInteractor
    @Inject
    internal lateinit var settingsRepository: SettingsRepository
    @Inject
    internal lateinit var localRepository: LocalRepository
    @Inject
    internal lateinit var tokenRepository: TokenRepository
    @Inject
    internal lateinit var saveServerInteractor: SaveConnectingServerInteractor
    @Inject
    internal lateinit var refreshSettingsInteractor: RefreshSettingsInteractor
    @Inject
    internal lateinit var getAccountsInteractor: GetAccountsInteractor
    @Inject
    internal lateinit var settingsInteractor: GetSettingsInteractor
    @Inject
    internal lateinit var saveCurrentServer: SaveCurrentServerInteractor
    @Inject
    internal lateinit var factory: RocketChatClientFactory
    @Inject
    internal lateinit var saveAccountInteractor: SaveAccountInteractor
    @Inject
    internal lateinit var analyticsManager: AnalyticsManager
    @Inject
    internal lateinit var dbManagerFactory: DatabaseManagerFactory
    @Inject
    internal lateinit var userHelper: UserHelper
}