package com.example.newsbara

import android.app.Application
import android.util.Log

class MyApp : Application() {

    init {
        Log.d("MyApp", "✅ MyApp created")
    }
    val sharedViewModel: SharedViewModel by lazy {
        SharedViewModel()
    }
}

