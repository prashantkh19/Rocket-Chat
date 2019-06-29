package chat.rocket.android.dagger

import android.app.Application
import chat.rocket.android.authentication.RocketChat
import chat.rocket.android.authentication.TemplateActivity
import chat.rocket.android.authentication.di.TemplateModule
import chat.rocket.android.dagger.module.AppModule
import chat.rocket.android.dagger.scope.PerActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@PerActivity
@Component(modules = [TemplateModule::class, AppModule::class]
)
interface RocketComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): RocketComponent

        @BindsInstance
        fun activity(templateActivity: TemplateActivity): Builder
    }

    fun inject(obj: RocketChat)

}
