package chat.rocket.android.dagger

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import chat.rocket.android.authentication.RocketChatWrapper
import chat.rocket.android.authentication.di.RocketChatModule
import chat.rocket.android.dagger.module.AppModule
import chat.rocket.android.dagger.scope.PerActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@PerActivity
@Component(modules = [RocketChatModule::class, AppModule::class]
)
interface RocketComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): RocketComponent

        @BindsInstance
        fun activity(activity: AppCompatActivity): Builder
    }

    fun inject(obj: RocketChatWrapper)

}
