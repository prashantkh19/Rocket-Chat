package com.example.sample

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import androidx.work.Worker
import chat.rocket.android.app.RocketChatApplication
import chat.rocket.android.app.RocketChatInjector
import dagger.android.AndroidInjector

class MyApplication : Application(), RocketChatInjector {

    override fun activityInjector(): AndroidInjector<Activity> = rocketChatApplication.activityInjector()

    override fun serviceInjector(): AndroidInjector<Service> = rocketChatApplication.serviceInjector()

    override fun broadcastReceiverInjector(): AndroidInjector<BroadcastReceiver> = rocketChatApplication.broadcastReceiverInjector()

    override fun workerInjector(): AndroidInjector<Worker> = rocketChatApplication.workerInjector()

    lateinit var rocketChatApplication: RocketChatApplication

    override fun onCreate() {
        super.onCreate()
        rocketChatApplication = RocketChatApplication.instance(this)
        rocketChatApplication.init()
    }

}