package com.example.newsbara

import android.app.Application
import android.util.Log

class MyApp : Application() {

    val sharedViewModel: SharedViewModel by lazy {
        SharedViewModel()
    }
}

