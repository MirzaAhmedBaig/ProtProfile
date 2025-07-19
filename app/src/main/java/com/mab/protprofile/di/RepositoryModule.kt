package com.mab.protprofile.di

import com.mab.protprofile.data.repository.AuthRepositoryImpl
import com.mab.protprofile.data.repository.MyDataRepositoryImpl
import com.mab.protprofile.domain.repository.AuthRepository
import com.mab.protprofile.domain.repository.MyDataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindAuthRepository(repository: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    abstract fun bindDataRepository(repository: MyDataRepositoryImpl): MyDataRepository
}
