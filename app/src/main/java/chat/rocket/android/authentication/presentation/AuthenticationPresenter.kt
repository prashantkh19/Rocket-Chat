package chat.rocket.android.authentication.presentation

import chat.rocket.android.R
import chat.rocket.android.analytics.AnalyticsManager
import chat.rocket.android.analytics.event.AuthenticationEvent
import chat.rocket.android.core.behaviours.showMessage
import chat.rocket.android.core.lifecycle.CancelStrategy
import chat.rocket.android.db.DatabaseManager
import chat.rocket.android.db.DatabaseManagerFactory
import chat.rocket.android.db.model.ChatRoomEntity
import chat.rocket.android.helper.UserHelper
import chat.rocket.android.infrastructure.LocalRepository
import chat.rocket.android.server.domain.*
import chat.rocket.android.server.domain.model.Account
import chat.rocket.android.server.infrastructure.RocketChatClientFactory
import chat.rocket.android.server.presentation.CheckServerPresenter
import chat.rocket.android.util.extension.launchUI
import chat.rocket.android.util.extensions.*
import chat.rocket.android.util.retryIO
import chat.rocket.common.RocketChatException
import chat.rocket.common.model.*
import chat.rocket.common.util.ifNull
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.rest.*
import chat.rocket.core.model.Myself
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class AuthenticationPresenter @Inject constructor(
        private val view: AuthenticationView,
        private val strategy: CancelStrategy,
        private val navigator: AuthenticationNavigator,
        private val getCurrentServerInteractor: GetCurrentServerInteractor,
        private val getAccountInteractor: GetAccountInteractor,
        private val settingsRepository: SettingsRepository,
        private val localRepository: LocalRepository,
        private val tokenRepository: TokenRepository,
        private val saveServerInteractor: SaveConnectingServerInteractor,
        refreshSettingsInteractor: RefreshSettingsInteractor,
        private val getAccountsInteractor: GetAccountsInteractor,
        val settingsInteractor: GetSettingsInteractor,
        private val saveCurrentServer: SaveCurrentServerInteractor,
        val factory: RocketChatClientFactory,
        private val saveAccountInteractor: SaveAccountInteractor,
        private val analyticsManager: AnalyticsManager,
        private val dbManagerFactory: DatabaseManagerFactory? = null,
        private val userHelper: UserHelper
        ) : CheckServerPresenter(
        strategy = strategy,
        factory = factory,
        versionCheckView = view,
        settingsInteractor = settingsInteractor,
        refreshSettingsInteractor = refreshSettingsInteractor
) {

    private lateinit var dbManager: DatabaseManager
    private val protocol = chat.rocket.android.util.protocol
    private var currentServer : String = "$protocol${chat.rocket.android.util.serverDomain
            .sanitize()}"
    private var token: Token? = null
    private lateinit var client: RocketChatClient
    private lateinit var settings: PublicSettings

    fun loadCredentials(callback: (isAuthenticated: Boolean) -> Unit) {
        launchUI(strategy) {
            val currentServer = getCurrentServerInteractor.get()
            val serverToken = currentServer?.let { tokenRepository.get(currentServer) }
            val settings = currentServer?.let { settingsRepository.get(currentServer) }
            val account = currentServer?.let { getAccountInteractor.get(currentServer) }

            account?.let {
                localRepository.save(LocalRepository.CURRENT_USERNAME_KEY, account.userName)
            }

            if (currentServer == null ||
                serverToken == null ||
                settings == null ||
                account?.userName == null
            ) {
                callback(false)
            } else {
                callback(true)
            }
        }
    }

    fun checkServer(server: String) {
        if (!server.isValidUrl()) {
            view.showInvalidServerUrlMessage()
        } else {
            view.showLoading()
            setupConnectionInfo(server)
            checkServerInfo(server)
        }
    }

    private fun setupConnection(serverUrl: String) {
        currentServer = serverUrl
        client = factory.get(currentServer)
        settings = settingsInteractor.get(currentServer)
        token = tokenRepository.get(currentServer)
    }

    fun connect(serverUrl: String) {
        connectToServer(serverUrl) {
            setupConnection(serverUrl)
            authenticateWithUserAndPassword(
                    chat.rocket.android.util.userEmail,
                    chat.rocket.android.util.userPassword
            )
        }
    }

    private fun connectToServer(serverUrl: String, block: () -> Unit) {
        if (!serverUrl.isValidUrl()) {
            view.showInvalidServerUrlMessage()
        } else {
            launchUI(strategy) {
                // Check if we already have an account for this server...
                val account = getAccountsInteractor.get().firstOrNull { it.serverUrl == serverUrl }
                if (account != null) {
                    toChatRoom()
                    return@launchUI
                }
                view.showLoading()
                try {
                    withContext(Dispatchers.Default) {
                        refreshServerAccounts()
                        checkEnabledAccounts(serverUrl)
                        checkIfLoginFormIsEnabled()
                        checkIfCreateNewAccountIsEnabled()
                        saveServerInteractor.save(serverUrl)
                        block()
                    }
                } catch (ex: Exception) {
                    view.showMessage(ex)
                } finally {
                    view.hideLoading()
                }
            }
        }
    }

    private fun authenticateWithUserAndPassword(usernameOrEmail: String, password: String) {
        launchUI(strategy) {
            view.showLoading()
            try {
                val token = retryIO("login") {
                    when {
                        settings.isLdapAuthenticationEnabled() ->
                            client.loginWithLdap(usernameOrEmail, password)
                        usernameOrEmail.isEmail() ->
                            client.loginWithEmail(usernameOrEmail, password)
                        else ->
                            client.login(usernameOrEmail, password)
                    }
                }
                val myself = retryIO("me()") { client.me() }
                myself.username?.let { username ->
                    val user = User(
                            id = myself.id,
                            roles = myself.roles,
                            status = myself.status,
                            name = myself.name,
                            emails = myself.emails?.map { Email(it.address ?: "",
                                    it.verified) },
                            username = username,
                            utcOffset = myself.utcOffset
                    )
                    localRepository.saveCurrentUser(currentServer, user)
                    saveCurrentServer.save(currentServer)
                    localRepository.save(LocalRepository.CURRENT_USERNAME_KEY, username)
                    saveAccount(username)
                    saveToken(token)
                    analyticsManager.logLogin(
                            AuthenticationEvent.AuthenticationWithUserAndPassword,
                            true
                    )
                    view.saveSmartLockCredentials(usernameOrEmail, password)
                    //to going to chat room
                    toChatRoom()
                }
            } catch (exception: RocketChatException) {
                    analyticsManager.logLogin(
                            AuthenticationEvent.AuthenticationWithUserAndPassword,
                            false
                    )
                    exception.message?.let {
                        if(it == "Unauthorized"){
                            view.showMessage("Requires Registration")
                            signup(chat.rocket.android.util.name,
                                    chat.rocket.android.util.userName,
                                    chat.rocket.android.util.userPassword,
                                    chat.rocket.android.util.userEmail)
                        }
                        view.showMessage(it)
                    }.ifNull {
                        view.showGenericErrorMessage()
                    }
            } finally {
                view.hideLoading()
            }
        }

    }

    private fun saveAccount(username: String) {
        val icon = settings.favicon()?.let {
            currentServer.serverLogoUrl(it)
        }
        val logo = settings.wideTile()?.let {
            currentServer.serverLogoUrl(it)
        }
        val thumb = currentServer.avatarUrl(username, token?.userId, token?.authToken)
        val account = Account(
                settings.siteName() ?: currentServer,
                currentServer,
                icon,
                logo,
                username,
                thumb
        )
        saveAccountInteractor.save(account)
    }

    private fun saveToken(token: Token) = tokenRepository.save(currentServer, token)

    private fun signup(name: String, username: String, password: String, email: String) {
        val client = factory.get(currentServer)
        launchUI(strategy) {
            view.showLoading()
            try {
                // TODO This function returns a user so should we save it?
                retryIO("signup") { client.signup(email, name, username, password) }
                // TODO This function returns a user token so should we save it?
                retryIO("login") { client.login(username, password) }
                val me = retryIO("me") { client.me() }
                saveCurrentServer.save(currentServer)
                localRepository.save(LocalRepository.CURRENT_USERNAME_KEY, me.username)
                saveAccount(me)
                analyticsManager.logSignUp(
                        AuthenticationEvent.AuthenticationWithUserAndPassword,
                        true
                )
                view.saveSmartLockCredentials(username, password)
                toChatRoom()
            } catch (exception: RocketChatException) {
                analyticsManager.logSignUp(
                        AuthenticationEvent.AuthenticationWithUserAndPassword,
                        false
                )
                exception.message?.let {
                    view.showMessage(it)
                }.ifNull {
                    view.showGenericErrorMessage()
                }
            } finally {
                view.hideLoading()

            }
        }
    }

    private fun saveAccount(me: Myself) {
        val icon = settings.favicon()?.let {
            currentServer.serverLogoUrl(it)
        }
        val logo = settings.wideTile()?.let {
            currentServer.serverLogoUrl(it)
        }
        val thumb = currentServer.avatarUrl(me.username!!, token?.userId, token?.authToken)
        val account = Account(
                settings.siteName() ?: currentServer,
                currentServer,
                icon,
                logo,
                me.username!!,
                thumb
        )
        saveAccountInteractor.save(account)
    }

    private fun getDbManager() {
        dbManagerFactory?.create(currentServer)?.let {
            dbManager = it
        }
    }

    fun toChatRoom() {
        setupConnection(currentServer)
        getDbManager()

        view.showMessage("Going to Chat Room")

        launchUI(strategy) {
            try {
                refreshChatRooms()
                loadChatRoom(chat.rocket.android.util.roomName)

            } catch (ex: Exception) {
                Timber.e(ex, "Error refreshing channels")
                view.showGenericErrorMessage()
            }
        }
    }

    private suspend fun refreshChatRooms() {
        val rooms = retryIO("fetch chatRooms", times = 10,
                initialDelay = 200, maxDelay = 2000) {
            client.chatRooms().update
        }
        Timber.d("Refreshing rooms: $rooms")
        dbManager.processRooms(rooms)
    }

    fun loadChatRoom(roomId: String) {
        synchronized(this) {
            launchUI(strategy) {
                try {
                    val room = dbManager.getRoomByName(roomId)
                    if (room != null) {
                        loadChatRoom(room.chatRoom, true)
                    } else {
                        //create new one
                        Timber.e("CREATE NEW CHANNEL")
                        view.showMessage("Need to create a new channel")
                        createChannel(roomTypeOf(RoomType.PRIVATE_GROUP),
                                chat.rocket.android.util.roomName, ArrayList(), false)
                    }
                } catch (ex: Exception) {
                    Timber.e(ex, "Error loading channel")
                    view.showGenericErrorMessage()
                }
            }
        }
    }

    suspend fun loadChatRoom(chatRoom: ChatRoomEntity, local: Boolean = false) {
        with(chatRoom) {
            val isDirectMessage = roomTypeOf(type) is RoomType.DirectMessage
            val roomName =
                    if (settings.useSpecialCharsOnRoom() ||
                            (isDirectMessage && settings.useRealName())) {
                        fullname ?: name
                    } else {
                        name
                    }
            val myself = getCurrentUser()
            if (myself?.username == null) {
                view.showMessage(R.string.msg_generic_error)
            } else {
                navigator.toChatRoom(
                        chatRoomId = id,
                        chatRoomName = roomName,
                        chatRoomType = type,
                        isReadOnly = readonly ?: false,
                        chatRoomLastSeen = lastSeen ?: -1,
                        isSubscribed = open,
                        isCreator = ownerId == myself.id || isDirectMessage,
                        isFavorite = favorite ?: false
                )
            }
        }
    }

    private suspend fun getCurrentUser(): User? {
        userHelper.user()?.let {
            return it
        }
        try {
            val myself = retryIO { client.me() }
            val user = User(
                    id = myself.id,
                    username = myself.username,
                    name = myself.name,
                    status = myself.status,
                    utcOffset = myself.utcOffset,
                    emails = null,
                    roles = myself.roles
            )
            localRepository.saveCurrentUser(url = currentServer, user = user)
        } catch (ex: RocketChatException) {
            Timber.e(ex)
        }
        return null
    }

    fun createChannel(
            roomType: RoomType,
            channelName: String,
            usersList: List<String>,
            readOnly: Boolean
    ) {
        launchUI(strategy) {
            view.showLoading()
            try {
                client.createChannel(roomType, channelName, usersList, readOnly)
                toChatRoom()
            } catch (exception: RocketChatException) {
                exception.message?.let {
                    view.showMessage(it)
                }.ifNull {
                    view.showGenericErrorMessage()
                }
            } finally {
                view.hideLoading()
            }
        }
    }
}