package com.example.app_1

import android.app.Application
import com.example.app_1.data.FirebaseRepository

class LostFoundApplication : Application() {
    var repository: FirebaseRepository? = null
        private set

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.FIREBASE_PROJECT_ID.isNotBlank() && BuildConfig.FIREBASE_APP_ID.isNotBlank() && BuildConfig.FIREBASE_API_KEY.isNotBlank()) {
            repository = FirebaseRepository(this)
        }
    }
}
