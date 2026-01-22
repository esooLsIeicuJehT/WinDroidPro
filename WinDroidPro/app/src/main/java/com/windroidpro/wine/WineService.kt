package com.windroidpro.wine

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.windroidpro.core.ClipboardBridge
import com.windroidpro.core.DxvkManager
import com.windroidpro.core.NetworkManager
import com.windroidpro.core.RegistryManager
import com.windroidpro.core.ServiceManager
import com.windroidpro.core.WslManager
import com.windroidpro.data.Container
import com.windroidpro.data.ContainerDao
import com.windroidpro.native_bridge.NativeBridge
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class WineService : Service() {

    @Inject lateinit var dxvkManager: DxvkManager
    @Inject lateinit var registryManager: RegistryManager
    @Inject lateinit var serviceManager: ServiceManager
    @Inject lateinit var networkManager: NetworkManager
    @Inject lateinit var wslManager: WslManager
    @Inject lateinit var clipboardBridge: ClipboardBridge
    @Inject lateinit var containerDao: ContainerDao

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("WineService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("WineService started")
        val containerId = intent?.getStringExtra("container_id")

        if (containerId != null) {
            serviceScope.launch {
                val container = containerDao.getContainerById(containerId)
                if (container != null) {
                    configureEnvironment(container)
                } else {
                    Timber.e("Container not found for ID: $containerId")
                }
            }
        } else {
            Timber.w("No container_id provided in intent")
        }

        return START_NOT_STICKY
    }

    // Called when a container is actually launching
    private fun configureEnvironment(container: Container) {
        Timber.i("Configuring environment for ${container.name}")

        // Native optimizations
        NativeBridge.optimizeMemory()
        if (container.enableBox64) {
            NativeBridge.setBox64Config(container.box64Preset)
        }

        dxvkManager.installDxvk(container)
        dxvkManager.installVkd3d(container)

        networkManager.configureNetworking(container)
        wslManager.configureWsl(container)

        clipboardBridge.startListening(container)
        serviceManager.startServices(container)
    }

    override fun onDestroy() {
        super.onDestroy()
        clipboardBridge.stopListening()
        serviceScope.cancel()
    }
}
