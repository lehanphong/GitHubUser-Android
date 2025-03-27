package ntd.molea.githubuser

import android.app.Application
import ntd.molea.githubuser.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class GitHubUserApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@GitHubUserApp)
            modules(appModule)
        }
    }
} 