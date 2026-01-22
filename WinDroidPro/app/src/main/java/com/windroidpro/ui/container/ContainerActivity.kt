package com.windroidpro.ui.container

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.windroidpro.ui.theme.WinDroidProTheme
import timber.log.Timber

class ContainerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("ContainerActivity created")
        setContent {
            WinDroidProTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ContainerScreen(
                        onBackClick = { finish() },
                        onContainerClick = { container ->
                            Toast.makeText(this@ContainerActivity, "Launching ${container.name}...", Toast.LENGTH_SHORT).show()
                            // Logic to launch container would go here
                        }
                    )
                }
            }
        }
    }
}
