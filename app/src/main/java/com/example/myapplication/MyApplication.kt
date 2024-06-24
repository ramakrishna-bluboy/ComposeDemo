package com.example.myapplication

import android.app.Application
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("MyApplication","===>>> Token: $token")
                // Send this token to your server for later use in sending notifications
            } else {
                // Handle errors
            }
        }
    }
}