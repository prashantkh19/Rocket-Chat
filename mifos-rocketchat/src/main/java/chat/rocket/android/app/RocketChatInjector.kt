package chat.rocket.android.app

import chat.rocket.android.dagger.injector.HasWorkerInjector
import dagger.android.HasActivityInjector
import dagger.android.HasBroadcastReceiverInjector
import dagger.android.HasServiceInjector

interface RocketChatInjector : HasActivityInjector, HasServiceInjector,
        HasBroadcastReceiverInjector, HasWorkerInjector