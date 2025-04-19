package com.test.composeads

import android.app.Application
import com.test.composeads.di.appModule
import com.test.composeads.utils.extensions.fetchRemoteConfig
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class BaseApplication : Application(){
    companion object{
        var isRemoteConfigFetched = MutableStateFlow(false)
    }
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BaseApplication)
            androidLogger()
            modules(listOf(appModule))
        }
        fetchRemoteConfig(onSuccessfulFetch = {
            isRemoteConfigFetched.value = true
        }, onFailed = {
            isRemoteConfigFetched.value = true
        })

    }
}