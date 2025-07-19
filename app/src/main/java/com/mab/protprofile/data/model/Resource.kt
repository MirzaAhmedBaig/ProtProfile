package com.mab.protprofile.data.model

sealed class Resource<out T> {
    class Success<out T>(
        val data: T,
    ) : Resource<T>()

    class Error<out T>(
        val exception: Throwable,
        val data: T? = null,
    ) : Resource<T>()

    class Loading<out T>(
        val data: T? = null,
    ) : Resource<T>()
}
