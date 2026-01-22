package com.windroidpro.ui.container

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.windroidpro.data.Container
import com.windroidpro.data.ContainerDao
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ContainerViewModel @Inject constructor(
    private val containerDao: ContainerDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val containers = containerDao.getAllContainers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createContainer(name: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = UUID.randomUUID().toString()
            val prefixPath = File(context.filesDir, "wine/$id").absolutePath

            // Create directory
            File(prefixPath).mkdirs()

            val newContainer = Container(
                id = id,
                name = name,
                description = description,
                prefixPath = prefixPath,
                wineVersion = "9.0-staging", // Default
                box64Preset = "Balanced" // Default
            )
            containerDao.insertContainer(newContainer)
        }
    }

    fun deleteContainer(container: Container) {
        viewModelScope.launch(Dispatchers.IO) {
            containerDao.deleteContainer(container)
            // Delete directory
            File(container.prefixPath).deleteRecursively()
        }
    }
}
