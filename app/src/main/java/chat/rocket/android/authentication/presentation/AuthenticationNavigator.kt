package chat.rocket.android.authentication.presentation

import chat.rocket.android.R
import chat.rocket.android.authentication.ui.AuthenticationActivity
import chat.rocket.android.chatroom.ui.chatRoomIntent

class AuthenticationNavigator(internal val activity: AuthenticationActivity) {

    fun toChatRoom(
            chatRoomId: String,
            chatRoomName: String,
            chatRoomType: String,
            isReadOnly: Boolean,
            chatRoomLastSeen: Long,
            isSubscribed: Boolean,
            isCreator: Boolean,
            isFavorite: Boolean
    ) {
        activity.startActivity(
                activity.chatRoomIntent(
                        chatRoomId,
                        chatRoomName,
                        chatRoomType,
                        isReadOnly,
                        chatRoomLastSeen,
                        isSubscribed,
                        isCreator,
                        isFavorite
                )
        )
        activity.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
        activity.finish()
    }
}
