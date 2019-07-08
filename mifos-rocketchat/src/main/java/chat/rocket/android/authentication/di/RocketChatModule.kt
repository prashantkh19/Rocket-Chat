package chat.rocket.android.authentication.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import chat.rocket.android.authentication.presentation.AuthenticationNavigator
import chat.rocket.android.core.lifecycle.CancelStrategy
import chat.rocket.android.dagger.scope.PerActivity
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Job

@Module
class RocketChatModule {

    @Provides
    @PerActivity
    fun provideAuthenticationNavigator(activity: AppCompatActivity) =
            AuthenticationNavigator(activity)

    @Provides
    @PerActivity
    fun provideJob() = Job()

    @Provides
    @PerActivity
    fun provideLifecycleOwner(activity: AppCompatActivity): LifecycleOwner = activity

    @Provides
    @PerActivity
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy =
            CancelStrategy(owner, jobs)

//    @Provides
//    @PerActivity
//    fun providesAuthenticationView(activity: TemplateActivity): RocketChatView {
//        return activity
//    }
}