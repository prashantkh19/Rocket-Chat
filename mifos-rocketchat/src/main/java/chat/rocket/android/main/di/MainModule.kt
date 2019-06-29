package chat.rocket.android.main.di

import androidx.lifecycle.LifecycleOwner
import chat.rocket.android.core.behaviours.AppLanguageView
import chat.rocket.android.core.lifecycle.CancelStrategy
import chat.rocket.android.dagger.scope.PerActivity
import chat.rocket.android.main.presentation.MainNavigator
import chat.rocket.android.main.ui.MainActivity
import chat.rocket.android.server.infrastructure.ConnectionManager
import chat.rocket.android.server.infrastructure.ConnectionManagerFactory
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Job
import javax.inject.Named

@Module
class MainModule {

    @Provides
    @PerActivity
    fun provideMainNavigator(activity: MainActivity) = MainNavigator(activity)

    @Provides
    @PerActivity
    fun appLanguageView(activity: MainActivity): AppLanguageView {
        return activity
    }

    @Provides
    @PerActivity
    fun provideJob() = Job()

    @Provides
    fun provideLifecycleOwner(activity: MainActivity): LifecycleOwner = activity

    @Provides
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy =
            CancelStrategy(owner, jobs)

    @Provides
    @PerActivity
    @Named("MainPresenter")
    fun provideConnectionManager(
            factory: ConnectionManagerFactory,
            @Named("currentServer") currentServer: String
    ): ConnectionManager {
        return factory.create(currentServer)
    }
}