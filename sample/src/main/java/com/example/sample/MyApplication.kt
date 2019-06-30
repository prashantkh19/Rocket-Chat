package com.example.sample

import android.app.Application
import chat.rocket.android.app.RocketChatApplication
import chat.rocket.android.app.RocketChatInjector

class MyApplication : Application(), RocketChatInjector {

    lateinit var rocketChatApplication: RocketChatApplication

    override fun onCreate() {
        super.onCreate()
        rocketChatApplication = RocketChatApplication(this)
        rocketChatApplication.init()
    }

    override fun setActivityInjector() = rocketChatApplication.activityInjector()

    override fun setServiceInjector() = rocketChatApplication.serviceInjector()

    override fun setBroadcastReceiverInjector() = rocketChatApplication.broadcastReceiverInjector()

    override fun setWorkerInjector() = rocketChatApplication.workerInjector()
}