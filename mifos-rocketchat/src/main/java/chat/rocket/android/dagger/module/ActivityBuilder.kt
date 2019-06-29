package chat.rocket.android.dagger.module

import chat.rocket.android.chatdetails.di.ChatDetailsFragmentProvider
import chat.rocket.android.chatinformation.di.MessageInfoFragmentProvider
import chat.rocket.android.chatinformation.ui.MessageInfoActivity
import chat.rocket.android.chatroom.di.ChatRoomFragmentProvider
import chat.rocket.android.chatroom.di.ChatRoomModule
import chat.rocket.android.chatroom.ui.ChatRoomActivity
import chat.rocket.android.dagger.scope.PerActivity
import chat.rocket.android.directory.di.DirectoryFragmentProvider
import chat.rocket.android.draw.main.di.DrawModule
import chat.rocket.android.draw.main.ui.DrawingActivity
import chat.rocket.android.favoritemessages.di.FavoriteMessagesFragmentProvider
import chat.rocket.android.files.di.FilesFragmentProvider
import chat.rocket.android.main.di.MainModule
import chat.rocket.android.main.ui.MainActivity
import chat.rocket.android.members.di.MembersFragmentProvider
import chat.rocket.android.mentions.di.MentionsFragmentProvider
import chat.rocket.android.pinnedmessages.di.PinnedMessagesFragmentProvider
import chat.rocket.android.profile.di.ProfileFragmentProvider
import chat.rocket.android.server.di.ChangeServerModule
import chat.rocket.android.server.ui.ChangeServerActivity
import chat.rocket.android.settings.di.SettingsFragmentProvider
import chat.rocket.android.settings.password.di.PasswordFragmentProvider
import chat.rocket.android.settings.password.ui.PasswordActivity
import chat.rocket.android.userdetails.di.UserDetailsFragmentProvider
import chat.rocket.android.videoconference.di.VideoConferenceModule
import chat.rocket.android.videoconference.ui.VideoConferenceActivity
import chat.rocket.android.webview.adminpanel.di.AdminPanelWebViewFragmentProvider
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {
//
//    @PerActivity
//    @ContributesAndroidInjector(modules = [TemplateModule::class])
//    abstract fun bindAuthenticationActivity(): TemplateActivity

    @PerActivity
    @ContributesAndroidInjector(
            modules = [MainModule::class,
                ProfileFragmentProvider::class,
                SettingsFragmentProvider::class,
                AdminPanelWebViewFragmentProvider::class,
                DirectoryFragmentProvider::class
            ]
    )
    abstract fun bindMainActivity(): MainActivity

    @PerActivity
    @ContributesAndroidInjector(
            modules = [ChatRoomModule::class,
                ChatRoomFragmentProvider::class,
                UserDetailsFragmentProvider::class,
                ChatDetailsFragmentProvider::class,
                MembersFragmentProvider::class,
                MentionsFragmentProvider::class,
                PinnedMessagesFragmentProvider::class,
                FavoriteMessagesFragmentProvider::class,
                FilesFragmentProvider::class
            ]
    )
    abstract fun bindChatRoomActivity(): ChatRoomActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [PasswordFragmentProvider::class])
    abstract fun bindPasswordActivity(): PasswordActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [ChangeServerModule::class])
    abstract fun bindChangeServerActivity(): ChangeServerActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [MessageInfoFragmentProvider::class])
    abstract fun bindMessageInfoActiviy(): MessageInfoActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [DrawModule::class])
    abstract fun bindDrawingActivity(): DrawingActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [VideoConferenceModule::class])
    abstract fun bindVideoConferenceActivity(): VideoConferenceActivity


}
