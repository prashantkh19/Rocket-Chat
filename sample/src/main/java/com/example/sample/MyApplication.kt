package com.example.sample

import chat.rocket.android.app.RocketChat
import chat.rocket.android.app.RocketChatApplication

class MyApplication : RocketChat() {

    override fun onCreate() {
        super.onCreate()
        initialize()
    }
}