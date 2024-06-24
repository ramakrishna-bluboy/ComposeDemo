package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.AppConstant.Companion.ACTION_MORE_INFO
import com.example.myapplication.AppConstant.Companion.EXTRA_NOTIFICATION_ACTION
import com.example.myapplication.AppConstant.Companion.EXTRA_NOTIFICATION_ID

class MainActivity : ComponentActivity() {

    private var shouldShowRationale by mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted
                sendNotification()
            } else {
                // Permission is denied
                shouldShowRationale =
                    !shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readIntentData()

        setContent {
            NotificationPermissionApp(
                shouldShowRationale = shouldShowRationale,
                onPermissionRequest = {
                    // Check and request notification permission
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        when {
                            ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                // Permission already granted
                                sendNotification()
                            }

                            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                                // Show rationale
                                shouldShowRationale = true
                            }

                            else -> {
                                // Request permission
                                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    } else {
                        // Show notification on lower API levels without explicit permission request
                        sendNotification()
                    }
                },
                onOpenSettings = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }
                    startActivity(intent)
                }
            )
        }
    }

    private fun readIntentData() {
        intent?.let {
            val notificationAction = it.getStringExtra(EXTRA_NOTIFICATION_ACTION)
            if (ACTION_MORE_INFO == notificationAction) {
                // Dismiss the notification
                val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)
                NotificationManagerCompat.from(this).cancel(notificationId)
            }
        }
    }

    private fun sendNotification() {
        NotificationHelper().sendRefillNotification(
            this,
            getString(R.string.refill_title),
            getString(R.string.refill_message)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPermissionApp(
    shouldShowRationale: Boolean,
    onPermissionRequest: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Demo for Refill from Notification") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (shouldShowRationale) {
                var showDialog by remember { mutableStateOf(true) }
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showDialog = false
                        },
                        title = {
                            Text(
                                text = "Enable Notifications",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        text = {
                            Text("To receive notifications, please enable the notification permission in settings.")
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    onOpenSettings()
                                    showDialog = false
                                }
                            ) {
                                Text("Enable")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showDialog = false
                                }
                            ) {
                                Text("No Thanks")
                            }
                        }
                    )
                }
            }
            Button(
                onClick = { onPermissionRequest() },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Send Refill Notification")
            }
        }
    }
}
