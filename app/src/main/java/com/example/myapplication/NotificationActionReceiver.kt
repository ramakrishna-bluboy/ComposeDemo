package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.AppConstant.Companion.ACTION_API_CALL
import com.example.myapplication.AppConstant.Companion.EXTRA_NOTIFICATION_ID
import com.example.myapplication.network.ApiResponse
import com.example.myapplication.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)
            when (intent.action) {
                ACTION_API_CALL -> makeApiCall(context, notificationId)
            }
        }
    }

    private fun makeApiCall(context: Context, notificationId: Int) {
        Log.d("NotificationActionReceiver", "===>>> Calling api.....")
        val call = RetrofitInstance.api.getData()
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    if (notificationId != -1) {
                        // Dismiss the notification
                        NotificationManagerCompat.from(context).cancel(notificationId)
                    }
                    response.body()?.let {
                        NotificationHelper().showResultNotification(
                            context,
                            "Refilled successfully"
                        )
                    }
                } else {
                    NotificationHelper().showResultNotification(context, "Refill failed")
                    Toast.makeText(context, "API call failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "API call failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
