package com.windroidpro.ui.usb

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsbDevicesScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val usbManager = remember { context.getSystemService(Context.USB_SERVICE) as? UsbManager }

    // Get list of connected devices
    // Note: This does not update dynamically on attach/detach in this simple implementation
    val usbDevices = remember { usbManager?.deviceList?.values?.toList() ?: emptyList() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("USB Devices") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (usbDevices.isEmpty()) {
             Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No USB devices found",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(usbDevices) { device ->
                    UsbDeviceItem(device)
                    Divider()
                }
            }
        }
    }
}

@Composable
fun UsbDeviceItem(device: UsbDevice) {
    ListItem(
        headlineContent = { Text(device.productName ?: "Unknown Device") },
        supportingContent = { Text("Vendor ID: ${device.vendorId}, Product ID: ${device.productId}") },
        leadingContent = { Icon(Icons.Default.Usb, contentDescription = null) }
    )
}
