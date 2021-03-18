package com.githubusers.android.domain.state

sealed class DataState<T>(
    var loading: Boolean,
    var data: T? = null,
    var errorType: ErrorType? = null,
    var stateMessage: StateMessage? = null
) {
    class LOADING<T>(
        isLoading: Boolean,
        cachedData: T? = null
    ) : DataState<T>(
        loading = isLoading,
        data = cachedData
    )

    class SUCCESS<T>(
        data: T? = null,
        stateMessage: StateMessage? = null
    ) : DataState<T>(
        loading = false,
        data = data,
        stateMessage = stateMessage
    )

    class ERROR<T>(
        stateMessage: StateMessage,
        errorType: ErrorType,
        cachedData: T? = null
    ) : DataState<T>(
        loading = false,
        data = cachedData,
        errorType = errorType,
        stateMessage = stateMessage
    )
}

sealed class ErrorType {
    object APP_ERROR : ErrorType()
    object NETWORK_ERROR : ErrorType()
}

data class StateMessage(
    val message: String?
)
