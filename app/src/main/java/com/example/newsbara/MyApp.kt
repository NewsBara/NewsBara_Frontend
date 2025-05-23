package com.example.newsbara

import android.app.Application
import androidx.lifecycle.ViewModelProvider

class MyApp : Application() {
    val sharedViewModel: SharedViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(this)
            .create(SharedViewModel::class.java)
    }
}
