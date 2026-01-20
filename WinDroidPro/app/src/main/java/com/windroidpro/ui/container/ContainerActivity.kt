package com.windroidpro.ui.container

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import timber.log.Timber

class ContainerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("ContainerActivity created")
        setContent {
            Text("Container Manager (Placeholder)")
        }
    }
}
