package chat.rocket.android.authentication.di

import androidx.lifecycle.LifecycleOwner
import chat.rocket.android.authentication.TemplateActivity
import chat.rocket.android.authentication.presentation.AuthenticationNavigator
import chat.rocket.android.authentication.presentation.AuthenticationView
import chat.rocket.android.core.lifecycle.CancelStrategy
import chat.rocket.android.dagger.scope.PerActivity
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Job

@Module
class TemplateModule {

    @Provides
    @PerActivity
    fun provideAuthenticationNavigator(activity: TemplateActivity) =
            AuthenticationNavigator(activity)

    @Provides
    @PerActivity
    fun provideJob() = Job()

    @Provides
    @PerActivity
    fun provideLifecycleOwner(activity: TemplateActivity): LifecycleOwner = activity

    @Provides
    @PerActivity
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy =
            CancelStrategy(owner, jobs)

    @Provides
    @PerActivity
    fun providesAuthenticationView(activity: TemplateActivity): AuthenticationView {
        return activity
    }
}