package com.windroidpro.di

import com.windroidpro.core.CommandExecutor
import com.windroidpro.native_bridge.NativeCommandExecutor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreModule {

    @Binds
    abstract fun bindCommandExecutor(
        nativeCommandExecutor: NativeCommandExecutor
    ): CommandExecutor
}
