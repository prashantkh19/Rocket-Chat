package chat.rocket.android.app

import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import androidx.work.Worker
import chat.rocket.android.dagger.injector.HasWorkerInjector
import dagger.android.AndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasBroadcastReceiverInjector
import dagger.android.HasServiceInjector

interface RocketChatInjector : HasActivityInjector, HasServiceInjector,
        HasBroadcastReceiverInjector, HasWorkerInjector {

    override fun activityInjector(): AndroidInjector<Activity> = setActivityInjector() as AndroidInjector<Activity>

    override fun serviceInjector(): AndroidInjector<Service> = setServiceInjector() as AndroidInjector<Service>

    override fun broadcastReceiverInjector(): AndroidInjector<BroadcastReceiver> = setBroadcastReceiverInjector() as AndroidInjector<BroadcastReceiver>

    override fun workerInjector(): AndroidInjector<Worker> = setWorkerInjector() as AndroidInjector<Worker>

    fun setActivityInjector(): Any

    fun setServiceInjector(): Any

    fun setBroadcastReceiverInjector(): Any

    fun setWorkerInjector(): Any

}