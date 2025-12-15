package com.windroidpro.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import com.windroidpro.ui.theme.WinDroidProTheme
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Timber.d("MainActivity created")
        
        setContent {
            WinDroidProTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WinDroid Pro") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to WinDroid Pro",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Advanced Windows Emulator for Android",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { /* TODO: Navigate to containers */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Manage Containers")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { /* TODO: Navigate to USB devices */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("USB Devices")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { /* TODO: Navigate to settings */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Settings")
            }
        }
    }
}